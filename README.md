# AK Deribit integration 
### Account on test.deribit.com
* e-mail: jemeba3913@otozuz.com
* password: wzGf5qLZw@my9wf
* username: AKcopperco

## Prerequisites
* PostgreSQL should be run on localhost with user having enough permissions to create schema and tables with username: postgres and password: mysecretpassword 

## Run application
./gradlew bootRun

## To run in container mode
./gradlew build && docker-compose build && docker-compose up

## Methods
### Get Balances
curl -X GET "http://localhost:8080/api/balances?client_id=miom49HG&client_secret=3nz4JfvojKwmcLHxn-aSdnyYRRin82X-ZCETTMIZfJU" -H "Content-Type: application/json"

Where:
* cliend_id: Deribit api key
* cliend_secret: Deribit api secret

Expected results - json array with the following fields:
* username: string 
* currency: string 
* balance: number
* reserved: number

### Get Transactions
curl -X GET "http://localhost:8080/api/transactions?client_id=miom49HG&client_secret=3nz4JfvojKwmcLHxn-aSdnyYRRin82X-ZCETTMIZfJU" -H "Content-Type: application/json"

Where:
* cliend_id: Deribit api key
* cliend_secret: Deribit api secret

Expected results - json array with the following fields:
* address: string
* amount: number
* currency: string
* state: string. Allowed values - completed/pending/replaced/rejected
* type: string. Allowed values - deposit/withdraw

### Withdraw
Withdraw address should be already in Deribit account address book. It is impossible to create it via api - method inside Internal api

curl -X POST --location "http://localhost:8080/api/withdraw" -H "Content-Type: application/json" -d "{ \"clientId\": \"miom49HG\", \"clientSecret\": \"3nz4JfvojKwmcLHxn-aSdnyYRRin82X-ZCETTMIZfJU\", \"currency\": \"BTC\", \"amount\": 0.07, \"address\": \"2Mz9oJZ7MPD2Bhq2zXV6jMmKgc8JtXn9i7o\" }"

Request payload:
{
  "cliendId": string,
  "cliendSecret": string,
  "currency": string,
  "amount" : number,
  "address": string
}

Expected result object with the following fields:
* address: string
* amount: number
* currency: string
* state: string

### Transfer to sub account 
Transfer requested amount to a sub-account. Sub account is looked up by deribit subaccount alias (username).

curl -X POST --location "http://localhost:8080/api/transfer-to-subaccount" -H "Content-Type: application/json" -d "{ \"clientId\": \"miom49HG\", \"clientSecret\": \"3nz4JfvojKwmcLHxn-aSdnyYRRin82X-ZCETTMIZfJU\", \"currency\": \"BTC\", \"amount\": 0.10, \"username\": \"AKcopperco_4\" }"

Request payload:
{
"cliendId": string,
"cliendSecret": string,
"currency": string,
"amount" : number,
"username": string
}

Expected result object with the following fields:
* amount: number,
* otherSide: string,
* currency: string,
* state: tring,
* type: tring,
* direction: string
