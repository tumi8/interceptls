#!/bin/bash

#create ca
openssl ecparam -name secp521r1 -genkey -noout -out ca-key.pem
openssl req -new -config ca.conf -extensions v3_ca -x509 -key ca-key.pem -out ca-cert.pem -days 730 -subj "/C=DE/ST=BY/L=MUC/O=TUM/CN=RootCA"

#create rsa server cert
openssl genrsa -out server-key-rsa.pem 2048
openssl req -config ssl.conf  -extensions req_ext -new -key server-key-rsa.pem -out server-cert-rsa.csr
openssl x509 -extfile ssl.conf -extensions req_ext -req -days 730 -in server-cert-rsa.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out server-cert-rsa.pem

#create ec server cert
openssl ecparam -name secp521r1 -genkey -noout -out server-key-ec.pem
openssl req -config ssl.conf -new -key server-key-ec.pem -out server-cert-ec.csr
openssl x509 -extfile ssl.conf -extensions req_ext -req -days 730 -in server-cert-ec.csr -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out server-cert-ec.pem

#create chains
cat ca-cert.pem server-cert-rsa.pem > server-cert-rsa.pem.chain
cat ca-cert.pem server-cert-ec.pem > server-cert-ec.pem.chain

#create keystore
openssl pkcs12 -export -out tls-rsa.p12 -in server-cert-rsa.pem -inkey server-key-rsa.pem -CAfile ca-cert.pem -chain -password pass:password
openssl pkcs12 -export -out tls-ec.p12 -in server-cert-ec.pem -inkey server-key-ec.pem -CAfile ca-cert.pem -chain -password pass:password
