#!/bin/bash

#create ca
openssl ecparam -name secp521r1 -genkey -noout -out ca-key.pem
openssl req -new -x509 -key ca-key.pem -out ca-cert.pem -days 730 -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=RootCA"

#create rsa server cert
openssl genrsa -out server-key-rsa.pem 2048
openssl req -new -key server-key-rsa.pem -out server-cert-rsa.csr -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=net.in.tum.de"
openssl x509 -req -days 730 -in server-cert-rsa.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out server-cert-rsa.pem

#create ec server cert
openssl ecparam -name secp521r1 -genkey -noout -out server-key-ec.pem
openssl req -new -key server-key-ec.pem -out server-cert-ec.csr -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=net.in.tum.de"
openssl x509 -req -days 730 -in server-cert-ec.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out server-cert-ec.pem

#create chains
cat ca-cert.pem server-cert-rsa.pem > server-cert-rsa.pem.chain
cat ca-cert.pem server-cert-ec.pem > server-cert-ec.pem.chain
