CREATE TABLE product
(
    id    BIGINT           NOT NULL,
    name  VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

