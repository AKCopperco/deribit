create table "deribit.user_balance"
(
    user_name varchar(255) not null,
    currency  varchar(8)   not null,
    balance   decimal,
    reserved  decimal,
    constraint "deribit.user_balance_pk"
        primary key (user_name, currency)
);

