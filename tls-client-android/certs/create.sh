#!/bin/bash

openssl ecparam -name secp521r1 -genkey -noout -out key-ec.pem
openssl req -new -x509 -key key-ec.pem -out cert-ec.pem -days 730 -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=net.in.tum.de"

openssl genrsa -out key-rsa.pem 2048
openssl req -new -x509 -key key-rsa.pem -out cert-rsa.pem -days 730 -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=net.in.tum.de"
