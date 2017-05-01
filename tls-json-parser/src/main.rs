extern crate nom;
extern crate tls_parser;

extern crate base64;

use base64::{encode, decode};

use std::env;
use std::fs::File;
use std::io::Read;
use std::io::Write;

#[macro_use]
extern crate serde_json;

use serde_json::Value;

use nom::IResult;
use tls_parser::parse_tls_plaintext;
use tls_parser::parse_tls_extensions;
use tls_parser::TlsMessage;
use tls_parser::TlsMessageHandshake;
use tls_parser::TlsPlaintext;
use tls_parser::TlsExtension;

pub fn parse_base64(base64: &String) -> Value {
    let bytes = decode(&base64).unwrap();
    return parse_raw(bytes);
}

fn main(){
	let args: Vec<_> = env::args().collect();
    if args.len() < 2 {
        panic!("No argument given.");
    }

	let json = parse_base64(&args[1]);
	//print result to std::out
	println!("{}", json.to_string());
}

fn parse_raw(bytes: Vec<u8>) -> Value {

	let res = parse_tls_plaintext(&bytes);
	let result = match_result(res);
	json!(result)
}

//recursive approach to parse all bytes
fn match_result<'a>(res:IResult<&[u8],TlsPlaintext<'a>>) -> Vec<Value> {

	let mut messages = Vec::new();

	match res {
		// rem is the remaining data (not parsed)
		// record is an object of type TlsRecord
		IResult::Done(rem,record) => {

			for msg in record.msg {
				match msg {
					TlsMessage::ChangeCipherSpec => {
						let change_cipher_json = json!({
								"type": "ClientKeyExchange"
						});
						messages.push(change_cipher_json);
					}
					TlsMessage::Alert(_) => panic!("Alert(_)"),
					TlsMessage::Handshake(handshake) => {
						match handshake {
							TlsMessageHandshake::HelloRequest => panic!("HelloRequest"),
							TlsMessageHandshake::EndOfEarlyData => panic!("EndOfEarlyData"),
							TlsMessageHandshake::ClientHello(client_hello) => {
								let extensions = match client_hello.ext {
									Some(x) => match_extensions(parse_tls_extensions(x)),
								    None    => json!({}) //Ignore
								};

								let hello_json = json!({
									"type" : "ClientHello",
									"version": client_hello.version,
									"ciphers": client_hello.ciphers,
									"ext": extensions,
								});

								messages.push(hello_json);
							},
							TlsMessageHandshake::ServerHello(server_hello) => {
								let extensions = match server_hello.ext {
									Some(x) => match_extensions(parse_tls_extensions(x)),
								    None    => json!({}) //Ignore
								};

								let hello_json = json!({
									"type": "ServerHello",
									"version": server_hello.version,
									"cipher": server_hello.cipher,
									"ext": extensions
								});

								messages.push(hello_json);
							}
							TlsMessageHandshake::ServerHelloV13(_) => panic!("ServerHelloV13(_)"),
							TlsMessageHandshake::NewSessionTicket(_) => panic!("NewSessionTicket(_)"),
							TlsMessageHandshake::HelloRetry(_) => panic!("HelloRetry(_)"),
							TlsMessageHandshake::Certificate(cert) => {
								let cert_json = json!({
										"type": "Certificate",
										"cert_chain": cert.cert_chain.iter().map(|x| encode(x.data)).collect::<Vec<String>>()
								});
								messages.push(cert_json);
							}
							TlsMessageHandshake::ServerKeyExchange(_) => {
								let key_exchange_json = json!({
										"type": "ServerKeyExchange"
								});
								messages.push(key_exchange_json);
							}
							TlsMessageHandshake::CertificateRequest(_) => panic!("CertificateRequest(_)"),
							TlsMessageHandshake::ServerDone(_) => {
								let done_json = json!({
										"type": "ServerDone"
								});
								messages.push(done_json);
							}
							TlsMessageHandshake::CertificateVerify(_) => panic!("CertificateVerify(_)"),
							TlsMessageHandshake::ClientKeyExchange(_) => {
								let key_exchange_json = json!({
										"type": "ClientKeyExchange"
								});
								messages.push(key_exchange_json);
							}
							TlsMessageHandshake::Finished(_) => panic!("Finished(_)"),
							TlsMessageHandshake::CertificateStatus(_) => panic!("CertificateStatus(_)"),
							TlsMessageHandshake::NextProtocol(_) => panic!("NextProtocol(_)"),
							TlsMessageHandshake::KeyUpdate(_) => panic!("KeyUpdate(_)"),

						}

					},
					TlsMessage::ApplicationData(_) => panic!("ApplicationData(_)"),
					TlsMessage::Heartbeat(_) => panic!("Heartbeat(_)"),
				};

				// match remaining bytes
				let mut res2 = match_result(parse_tls_plaintext(&rem));
				messages.append(&mut res2);

			}
		},
		IResult::Incomplete(_) => {
            let mut stderr = std::io::stderr();
            writeln!(&mut stderr, "Defragmentation required (TLS record)").unwrap();
		},
		IResult::Error(e) => {
            let mut stderr = std::io::stderr();
            writeln!(&mut stderr, "parse_tls_record_with_header failed: {:?}",e).unwrap();
		}

	}

	messages
}

fn match_extensions(ext: IResult<&[u8],Vec<TlsExtension>>) -> Value {
	let mut data = json!({});
	match ext {
		IResult::Done(rem,record) => {

			for ext in record {
				match ext {
					TlsExtension::SNI(sni) => {
						data["SNI"] = json!(sni);
					}
					TlsExtension::MaxFragmentLength(_) => panic!("MaxFragmentLength"),
					TlsExtension::StatusRequest(_) => panic!("StatusRequest"),
					TlsExtension::EllipticCurves(curves) => {
						data["EllipticCurves"] = json!(curves);
					}
					TlsExtension::EcPointFormats(formats) => {
						data["EcPointFormats"] = json!(formats);
					}
					TlsExtension::SignatureAlgorithms(algs) => {
						data["SignatureAlgorithms"] = json!(algs);
					}
					TlsExtension::SessionTicket(_) => panic!("SessionTicket"),
					TlsExtension::KeyShare(_) => panic!("KeyShare"),
					TlsExtension::PreSharedKey(_) => panic!("PreSharedKey"),
					TlsExtension::EarlyData(_) => panic!("EarlyData"),
					TlsExtension::SupportedVersions(_) => panic!("SupportedVersions"),
					TlsExtension::Cookie(_) => panic!("Cookie"),
					TlsExtension::PskExchangeModes(_) => panic!("PskExchangeModes"),
					TlsExtension::Heartbeat(_) => panic!("Heartbeat"),
					TlsExtension::ALPN(_) => panic!("ALPN"),
					TlsExtension::SignedCertificateTimestamp(_) => panic!("SignedCertificateTimestamp"),
					TlsExtension::Padding(_) => panic!("Padding"),
					TlsExtension::EncryptThenMac => panic!("EncryptThenMac"),
					TlsExtension::ExtendedMasterSecret => panic!("ExtendedMasterSecret"),
					TlsExtension::OidFilters(_) => panic!("OidFilters"),
					TlsExtension::NextProtocolNegotiation => panic!("NextProtocolNegotiation"),
					TlsExtension::RenegotiationInfo(info) => {
						data["RenegotiationInfo"] = json!(info);
					}
					TlsExtension::Unknown(_, _) => panic!("Unknown"),
				}

			}


		},
		IResult::Incomplete(_) => {
			panic!("parse_tls_extensions defragmentation required (TLS record)");
		},
		IResult::Error(e) => {
			panic!("parse_tls_extensions failed: {:?}",e);
		}
	}

	data

}


#[test]
fn test_golem_client() {
	let bytes = read_file("exampleHandshakes/golem/client.raw");
	let json = parse_base64(&encode(&bytes));

	println!("{}",json[0]["ciphers"]);
	assert_eq!(json!([49195,49187,49161,49199,49191,49171,162,64,50,158,103,51,156,60,47,255]),json[0]["ciphers"]);
}

#[test]
fn test_golem_server() {
	let bytes = read_file("exampleHandshakes/golem/server.raw");
	let json = parse_base64(&encode(&bytes));

	println!("{}",json[0]["cipher"]);
	assert_eq!(json!(49199),json[0]["cipher"]);
}

fn read_file(file: &str) -> Vec<u8>{
	let mut bytes = Vec::new();
	let mut f = File::open(file).expect("Unable to open file");
	f.read_to_end(&mut bytes).expect("Unable to read data");
	bytes
}
