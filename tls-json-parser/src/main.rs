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
	print!("{}", json.to_string());
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
								"type": "ChangeCipherSpec"
						});
						messages.push(change_cipher_json);
					}
					TlsMessage::Alert(_) => {
                        let json = json!({"type": "Alert"});
                        messages.push(json);
                    }
					TlsMessage::Handshake(handshake) => {
						match handshake {
							TlsMessageHandshake::HelloRequest => {
								let json = json!({"type": "HelloRequest"});
								messages.push(json);
							}
							TlsMessageHandshake::EndOfEarlyData => {
								let json = json!({"type": "EndOfEarlyData"});
								messages.push(json);
							}
							TlsMessageHandshake::ClientHello(client_hello) => {
								let extensions = match client_hello.ext {
									Some(x) => match_extensions(parse_tls_extensions(x)),
								    None    => json!({}) //Ignore
								};

								let hello_json = json!({
									"type" : "ClientHello",
									"version": client_hello.version,
									"ciphers": client_hello.ciphers,
                                    "compressions": client_hello.comp,
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
                                    "compression": server_hello.compression,
									"ext": extensions
								});

								messages.push(hello_json);
							}
							TlsMessageHandshake::ServerHelloV13(_) => {
								let json = json!({"type": "ServerHelloV13"});
								messages.push(json);
							}
							TlsMessageHandshake::NewSessionTicket(_) => {
								let json = json!({"type": "NewSessionTicket"});
								messages.push(json);
							}
							TlsMessageHandshake::HelloRetry(_) => {
								let json = json!({"type": "HelloRetry"});
								messages.push(json);
							}
							TlsMessageHandshake::Certificate(cert) => {
								let cert_json = json!({
										"type": "Certificate",
										"cert_chain": cert.cert_chain.iter().map(|x| encode(x.data)).collect::<Vec<String>>()
								});
								messages.push(cert_json);
							}
							TlsMessageHandshake::ServerKeyExchange(_) => {
								let json = json!({"type": "ServerKeyExchange"});
								messages.push(json);
							}
							TlsMessageHandshake::CertificateRequest(_) =>  {
								let json = json!({"type": "CertificateRequest"});
								messages.push(json);
							}
							TlsMessageHandshake::ServerDone(_) => {
								let done_json = json!({
										"type": "ServerDone"
								});
								messages.push(done_json);
							}
							TlsMessageHandshake::CertificateVerify(_) => {
								let json = json!({"type": "CertificateVerify"});
								messages.push(json);
							}
							TlsMessageHandshake::ClientKeyExchange(_) => {
								let json = json!({"type": "ClientKeyExchange"});
								messages.push(json);
							}
							TlsMessageHandshake::Finished(_) => {
								let json = json!({"type": "Finished"});
								messages.push(json);
							}
							TlsMessageHandshake::CertificateStatus(_) => {
								let json = json!({"type": "CertificateStatus"});
								messages.push(json);
							}
							TlsMessageHandshake::NextProtocol(_) => {
								let json = json!({"type": "NextProtocol"});
								messages.push(json);
							}
							TlsMessageHandshake::KeyUpdate(_) => {
								let json = json!({"type": "KeyUpdate"});
								messages.push(json);
							}

						}

					},
					TlsMessage::ApplicationData(_) => {
                        let json = json!({"type": "ApplicationData"});
                        messages.push(json);
                    }
					TlsMessage::Heartbeat(_) => {
                        let json = json!({"type": "Heartbeat"});
                        messages.push(json);
                    }
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
		IResult::Done(_rem,record) => {

			for ext in record {
				match ext {
					TlsExtension::SNI(sni) => {
                        data["sni"] = json!(sni);
					}
					TlsExtension::MaxFragmentLength(mfl) => {
                        data["maxFragmentLength"] = json!(mfl);
                    }
					TlsExtension::StatusRequest(sr) => {
                        data["statusRequest"] = json!(sr);
                    },
					TlsExtension::EllipticCurves(curves) => {
						data["ellipticCurves"] = json!(curves);
					}
					TlsExtension::EcPointFormats(formats) => {
						data["ecPointFormats"] = json!(formats);
					}
					TlsExtension::SignatureAlgorithms(algs) => {
						data["signatureAlgorithms"] = json!(algs);
					}
					TlsExtension::SessionTicket(st) => {
                        data["sessionTicket"] = json!(st);
                    }
					TlsExtension::KeyShare(ks) => {
                        data["keyShare"] = json!(ks);
                    }
					TlsExtension::PreSharedKey(psk) => {
                        data["preSharedKey"] = json!(psk);
                    }
					TlsExtension::EarlyData(ed) => {
                        data["earlyData"] = json!(ed);
                    }
					TlsExtension::SupportedVersions(sv) => {
                        data["supportedVersions"] = json!(sv);
                    }
					TlsExtension::Cookie(c) => {
                        data["cookie"] = json!(c);
                    }
					TlsExtension::PskExchangeModes(pskem) => {
                        data["pskExchangeModes"] = json!(pskem);
                    }
					TlsExtension::Heartbeat(hb) => {
                        data["heartbeat"] = json!(hb);
                    }
					TlsExtension::ALPN(alpn) => {
                        data["alpn"] = json!(alpn);
                    }
					TlsExtension::SignedCertificateTimestamp(stamp) => {
                        data["signedCertificateTimestamp"] = json!(stamp);
                    }
					TlsExtension::Padding(p) => {
                        data["padding"] = json!(p);
                    }
					TlsExtension::EncryptThenMac => {
                        data["encryptThenMac"] = json!(true);
                    }
					TlsExtension::ExtendedMasterSecret => {
                        data["extendedMasterSecret"] = json!(true);
                    }
					TlsExtension::OidFilters(f) => {
                        let mut oid_vec = Vec::new();
                        for s in f {
                            oid_vec.push(json!({
                                "cert_ext_oid": s.cert_ext_oid,
                                "cert_ext_val": s.cert_ext_val
                            }));
                        }
                        data["oidFilters"] = json!(oid_vec);
                    }
					TlsExtension::NextProtocolNegotiation => {
                        data["nextProtocolNegotiation"] = json!(true);
                    }
					TlsExtension::RenegotiationInfo(info) => {
						data["denegotiationInfo"] = json!(info);
					}
					TlsExtension::Unknown(ext_type, ext_data) => {
                        data["unknown"] = json!([ext_type, ext_data]);
                    }
				}

			}


		},
		IResult::Incomplete(_) => {
            let mut stderr = std::io::stderr();
            writeln!(&mut stderr, "parse_tls_extensions defragmentation required (TLS record)").unwrap();
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

	assert_eq!(json!([49195,49187,49161,49199,49191,49171,162,64,50,158,103,51,156,60,47,255]),json[0]["ciphers"]);
    assert_eq!(json!([23, 24]), json[0]["ext"]["ellipticCurves"]);
}

#[test]
fn test_golem_server() {
	let bytes = read_file("exampleHandshakes/golem/server.raw");
	let json = parse_base64(&encode(&bytes));

	assert_eq!(json!(49199),json[0]["cipher"]);
}

#[test]
fn test_ssllabs_client() {
	let bytes = read_file("exampleHandshakes/ssllabs/client.raw");
	let json = parse_base64(&encode(&bytes));

	assert_eq!(json!([49195,49187,49161,49199,49191,49171,162,64,50,158,103,51,156,60,47,255]),json[0]["ciphers"]);
    assert_eq!(json!([23, 24]), json[0]["ext"]["ellipticCurves"]);
}

#[test]
fn test_ssllabs_server() {
	let bytes = read_file("exampleHandshakes/ssllabs/server.raw");
	let json = parse_base64(&encode(&bytes));

	assert_eq!(json!(49199),json[0]["cipher"]);
}

fn read_file(file: &str) -> Vec<u8>{
	let mut bytes = Vec::new();
	let mut f = File::open(file).expect("Unable to open file");
	f.read_to_end(&mut bytes).expect("Unable to read data");
	bytes
}
