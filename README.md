# AK Deribit integration 
### Account on test.deribit.com
* e-mail: jemeba3913@otozuz.com
* password: wzGf5qLZw@my9wf
* username: AKcopperco

## Prerequisites
* PostgreSQL should be run on localhost with user having enough permissions to create schema and tables with username: postgres and password: mysecretpassword 

## Run application
./gradlew bootRun

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
