-- ADD Account

INSERT INTO account (id, account_balance) VALUES (1,10000);

-- ADD ATM Data
INSERT INTO atm (id, location) VALUES (1,'Location1');

INSERT INTO ATM_DENOMINATION_MAPPING (atm_id,amount,denomination) values (1,20,'50'), (1,20,'200'), (1,10,'100'), (1,100,'10') ;

-- ADD Transaction

INSERT INTO transaction (id, transaction_type, amount, account_id, transaction_date) VALUES (1, 'DEPOSIT_WITHDRAWAL', 1000, 1, '2020-12-05 18:10:50');