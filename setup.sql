CREATE TABLE "currency"
(
	"currency_id" integer PRIMARY KEY,
	"currency_name" character varying NOT NULL,
	"country" character varying NOT NULL,
	"symbol" character(3) NOT NULL
);

CREATE TABLE "exchange_rate"
(
	"id" serial PRIMARY KEY,
	"currency_id" integer,
	"unit" int NOT NULL,
	"buying_rate" double precision NOT NULL,
	"selling_rate" double precision NOT NULL,
	CONSTRAINT fk FOREIGN KEY (currency_id) REFERENCES currency(currency_id)
);

CREATE TABLE "exchange_transactions"
(
	"id" serial PRIMARY KEY,
	"currency_id" integer NOT NULL,
	"amount_bought" double precision NOT NULL,
	"amount_sold" double precision NOT NULL,
	"transaction_type" character varying NOT NULL,
	CONSTRAINT fk FOREIGN KEY (currency_id) REFERENCES currency(currency_id)
);

INSERT INTO currency VALUES (0, 'Euro', 'European Union', 'EUR');
INSERT INTO exchange_rate (currency_id, unit, buying_rate, selling_rate) VALUES (0, 1, 1, 1);
