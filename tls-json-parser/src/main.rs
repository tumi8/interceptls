extern crate nom;
extern crate tls_parser;

extern crate rustc_serialize as serialize;

use serialize::base64::{self, ToBase64};

#[macro_use]
extern crate serde_json;

use std::fs::File;
use std::io::Read;

use serde_json::Value;

use nom::IResult;
use tls_parser::parse_tls_plaintext;
use tls_parser::parse_tls_extensions;
use tls_parser::TlsMessage;
use tls_parser::TlsMessageHandshake;
use tls_parser::TlsPlaintext;
use tls_parser::TlsExtension;


fn main(){

	let mut bytes = Vec::new();
	let mut f = File::open("exampleHandshakes/golem/client.raw").expect("Unable to open file");
	f.read_to_end(&mut bytes).expect("Unable to read data");
	println!("Bytes to parse: {}", bytes.len());

	let res = parse_tls_plaintext(&bytes);
	let result_json = match_result(res);

	//for the moment print to console
	println!("{}",result_json.to_string());

}

//recursive approach to parse all bytes
fn match_result<'a>(res:IResult<&[u8],TlsPlaintext<'a>>) -> Value {

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
					TlsMessage::Alert(_) => println!("Alert(_)"),
					TlsMessage::Handshake(handshake) => {
						match handshake {
							TlsMessageHandshake::HelloRequest => println!("HelloRequest"),
							TlsMessageHandshake::EndOfEarlyData => println!("EndOfEarlyData"),
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
							TlsMessageHandshake::ServerHelloV13(_) => println!("ServerHelloV13(_)"),
							TlsMessageHandshake::NewSessionTicket(_) => println!("NewSessionTicket(_)"),
							TlsMessageHandshake::HelloRetry(_) => println!("HelloRetry(_)"),
							TlsMessageHandshake::Certificate(cert) => {
								let cert_json = json!({
										"type": "Certificate",
										"cert_chain": cert.cert_chain.iter().map(|x| x.data.to_base64(base64::STANDARD)).collect::<Vec<String>>()
								});
								messages.push(cert_json);
							}
							TlsMessageHandshake::ServerKeyExchange(_) => {
								let key_exchange_json = json!({
										"type": "ServerKeyExchange"
								});
								messages.push(key_exchange_json);
							}
							TlsMessageHandshake::CertificateRequest(_) => println!("CertificateRequest(_)"),
							TlsMessageHandshake::ServerDone(_) => {
								let done_json = json!({
										"type": "ServerDone"
								});
								messages.push(done_json);
							}
							TlsMessageHandshake::CertificateVerify(_) => println!("CertificateVerify(_)"),
							TlsMessageHandshake::ClientKeyExchange(_) => {
								let key_exchange_json = json!({
										"type": "ClientKeyExchange"
								});
								messages.push(key_exchange_json);
							}
							TlsMessageHandshake::Finished(_) => println!("Finished(_)"),
							TlsMessageHandshake::CertificateStatus(_) => println!("CertificateStatus(_)"),
							TlsMessageHandshake::NextProtocol(_) => println!("NextProtocol(_)"),
							TlsMessageHandshake::KeyUpdate(_) => println!("KeyUpdate(_)"),

						}

					},
					TlsMessage::ApplicationData(_) => println!("ApplicationData(_)"),
					TlsMessage::Heartbeat(_) => println!("Heartbeat(_)"),
				};

				// match remaining bytes
				let res2 = parse_tls_plaintext(&rem);
				match_result(res2);

			}

		},
		IResult::Incomplete(_) => {
			println!("Defragmentation required (TLS record)");
		},
		IResult::Error(e) => {
			println!("parse_tls_record_with_header failed: {:?}",e);
		}

	}

	json!(messages)

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
					TlsExtension::MaxFragmentLength(_) => println!("MaxFragmentLength"),
					TlsExtension::StatusRequest(_) => println!("StatusRequest"),
					TlsExtension::EllipticCurves(curves) => {
						data["EllipticCurves"] = json!(curves);
					}
					TlsExtension::EcPointFormats(formats) => {
						data["EcPointFormats"] = json!(formats);
					}
					TlsExtension::SignatureAlgorithms(algs) => {
						data["SignatureAlgorithms"] = json!(algs);
					}
					TlsExtension::SessionTicket(_) => println!("SessionTicket"),
					TlsExtension::KeyShare(_) => println!("KeyShare"),
					TlsExtension::PreSharedKey(_) => println!("PreSharedKey"),
					TlsExtension::EarlyData(_) => println!("EarlyData"),
					TlsExtension::SupportedVersions(_) => println!("SupportedVersions"),
					TlsExtension::Cookie(_) => println!("Cookie"),
					TlsExtension::PskExchangeModes(_) => println!("PskExchangeModes"),
					TlsExtension::Heartbeat(_) => println!("Heartbeat"),
					TlsExtension::ALPN(_) => println!("ALPN"),
					TlsExtension::SignedCertificateTimestamp(_) => println!("SignedCertificateTimestamp"),
					TlsExtension::Padding(_) => println!("Padding"),
					TlsExtension::EncryptThenMac => println!("EncryptThenMac"),
					TlsExtension::ExtendedMasterSecret => println!("ExtendedMasterSecret"),
					TlsExtension::OidFilters(_) => println!("OidFilters"),
					TlsExtension::NextProtocolNegotiation => println!("NextProtocolNegotiation"),
					TlsExtension::RenegotiationInfo(info) => {
						data["RenegotiationInfo"] = json!(info);
					}
					TlsExtension::Unknown(_, _) => println!("Unknown"),
				}

			}


		},
		IResult::Incomplete(_) => {
			println!("parse_tls_extensions defragmentation required (TLS record)");
		},
		IResult::Error(e) => {
			println!("parse_tls_extensions failed: {:?}",e);
		}
	}

	data

}
