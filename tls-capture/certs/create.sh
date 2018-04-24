#!/bin/bash

#------------------------------------------------
# CA for analysis server, including two server certs (RSA & ECDSA)
#------------------------------------------------
#create ca
openssl ecparam -name secp521r1 -genkey -noout -out ca-key.pem
openssl req -new -config ca.conf -extensions v3_ca -x509 -key ca-key.pem -out ca-cert.pem -days 730 -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=RootCA"

#create rsa server cert
openssl genrsa -out analysis-server-key-rsa.pem 2048
openssl req -config analysis-server.conf  -extensions req_ext -new -key analysis-server-key-rsa.pem -out analysis-server-cert-rsa.csr
openssl x509 -extfile analysis-server.conf -extensions req_ext -req -days 730 -in analysis-server-cert-rsa.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out analysis-server-cert-rsa.pem

#create ec server cert
openssl ecparam -name secp521r1 -genkey -noout -out analysis-server-key-ec.pem
openssl req -config analysis-server.conf -new -key analysis-server-key-ec.pem -out analysis-server-cert-ec.csr
openssl x509 -extfile analysis-server.conf -extensions req_ext -req -days 730 -in analysis-server-cert-ec.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out analysis-server-cert-ec.pem

#create chains
cat ca-cert.pem analysis-server-cert-rsa.pem > analysis-server-cert-rsa.pem.chain
cat ca-cert.pem analysis-server-cert-ec.pem > analysis-server-cert-ec.pem.chain

#create keystore
openssl pkcs12 -export -out tls-rsa.p12 -in analysis-server-cert-rsa.pem -inkey analysis-server-key-rsa.pem -CAfile ca-cert.pem -chain -password pass:password
openssl pkcs12 -export -out tls-ec.p12 -in analysis-server-cert-ec.pem -inkey analysis-server-key-ec.pem -CAfile ca-cert.pem -chain -password pass:password

#------------------------------------------------
# self-signed cert for capture-server (RSA & ECDSA)
#------------------------------------------------
openssl genrsa -out capture-server-key-rsa.pem 2048
openssl req -config capture-server.conf -extensions req_ext -x509 -newkey rsa:2048 -key capture-server-key-rsa.pem -out capture-server-cert-rsa.pem -days 730

openssl ecparam -name secp521r1 -genkey -param_enc explicit -out capture-server-key-ec.pem
openssl req -config capture-server.conf -extensions req_ext -new -x509 -key capture-server-key-ec.pem -out capture-server-cert-ec.pem -days 730

