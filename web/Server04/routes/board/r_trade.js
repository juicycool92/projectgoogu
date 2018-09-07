module.exports = function(app,passport,sessionManager,tradeModule,jsonWriter,format){
	app.get('/trade',function(req,res){
		sessionManager.sessionCheck(req,function(login,username){
			res.render('./trade/tradeForm',{'login':login,'userId':username});
		});
	});
	app.post('/trade/upload',function(req,res){
		sessionManager.sessionCheck(req,function(login,username){
			tradeModule.uploadTrade(req,username,function(err,result){
				if(err){
					console.log('err : '+err);
				}else{
					console.log('success :'+result);
				}
				sessionManager.sessionCheck(req,function(login,username){
					res.render('./trade/tradeForm',{'login':login,'userId':username});
				});
			});
		});
	});
	app.get('/tradeList',function(req,res){
		console.log('hellllo');
		var listNum = req.param('page');
		
		sessionManager.sessionCheck(req,function(login,username){
			if(listNum===null || !listNum){
				res.render('index',{'login':login,'userId':username});
			}else if(!login || login===false || username==='guest'){
				//비 로그인 상태에서 이 창울 띄었을때 이다.
				//오류를 보내자
				console.log('trade:login user is guest');
				res.render('index',{'login':login,'userId':username});
			}else{
				console.log('login user auto complete');
				tradeModule.getMyTradeList(username,listNum,function(err,result,listPerPage,totalSize){
					if(!err || err === null || result!== null || listPerPage !== null || totalSize !== null){
						console.log('sibal : '+result);
						jsonWriter.writeForMyTradeList(result,function(err,jsonFiles){
							if(!err || err === null || jsonFiles!== null){
								var listSize;
								if(totalSize%listPerPage>0){
									listSize = totalSize / listPerPage + 1;
									listSize = parseInt(listSize);
								}else{
									listSize = parseInt(listSize);
								} 
								console.log(jsonFiles);
								res.render('./trade/tradeList',{'login':login,'userId':username,'curPage':listNum,'totPage':listSize,'tradeList':JSON.parse(jsonFiles),"listPerPage":listPerPage});
							}else{
								//오류를 보내자
								console.log('trade:jsonwriteFailed');
								res.render('index',{'login':login,'userId':username});
							}
						});
					}else{
						console.log('trade:getting null by tradeModule.getMyTradeList');
						//오류를 보내자
						res.render('index',{'login':login,'userId':username});
						return;
					}
				});
			}
		});
	});
	
	app.post('/tradeList',function(req,res){
		tradeModule.getModuleByNum(0,req.body.tradeNum,function(err,result){
			if(err || !result){
				console.log('is it false?'+req.body.tradeNum);
				
				res.write('<html lang="utf-8"><script>alert("wrong");window.close();</script>');
				res.send();
			}else{
				sessionManager.sessionCheck(req,function(login,username){
					tradeModule.isDriver(username,function(errDriver){
						if(errDriver!==null){
							res.render('./trade/tradeInfo',{'isDriver':false,'tradeNum':req.body.tradeNum, 'userId':username});
						}else{
							res.render('./trade/tradeInfo',{'isDriver':true,'tradeNum':req.body.tradeNum, 'userId':username});
						}
					});
				});
			}
		});
	});
	
	app.post('/tradeListDetail',function(req,res){
		tradeModule.getModuleByNum(0,req.body.tradeNum,function(err,result){
			if(err || !result){

			}else{
				jsonWriter.createJsonforTradeDetail(result,function(resultString){
					res.json(resultString);
					res.send();
				});
			}
		});
	});
	app.post('/myTradeView',function(req,res){
		var tradeNum = req.body.tradeNum;
		console.log('he');
		sessionManager.sessionCheck(req,function(login,username){
			if(login !== null || login===true){
				tradeModule.getUserPosTrade(tradeNum,username,function(err,position){
					if(!err || position!==null){
						if(position==='sender'){
							tradeModule.getTradeAsOwner(tradeNum,function(err2,result){
								if(!err2 || result !== null){
									console.log('여기니1' + JSON.stringify(result));
									res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':position,'value':result});
								}else{
									console.log('여기니2');
									res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':position,'value':null});
								}
							});
						}else if(position === 'driver'){
							tradeModule.getTradeAsDriver(tradeNum,function(err2,result){
								if(!err2 || result !== null){
									console.log('여기니3');
									res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':position,'value':result});
								}else{
									console.log('여기니4');
									res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':position,'value':null});
								}
							});
						}
					}else{
						console.log('여기니5');
						res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':position,'value':null});
					}
				});
			}else{
				console.log('여기니6');
				res.render('./trade/myTradeInfo',{'login':login,'userId':username,'auth':null,'value':null});
			}
		});
	});
	
	
	app.put('/tradeRequestAccept',function(req,res){
		sessionManager.sessionCheck(req,function(login,username){
			tradeModule.isDriver(username,function(errDriver){
				//console.log('hello? :'+username+req.body.tradeNum+req.body.driverId);
				if(errDriver!==null){
					res.send(false);
					//드라이버가맞으면! 이제 상태창 업데이트를 하자
				}else{
					tradeModule.getTradeStatus(req.body.tradeNum,function(err,tradeStatus){
						if(tradeStatus === '0'){
							tradeModule.driverRequestTrade(req,function(err,results){
								if(err || results===null){
									console.log('here 64');
								}else{
									console.log('here66');
									res.send(true);
								}
							});
						}else{
							console.log('status is already :'+tradeStatus);
							res.send(false);
						}
					});
					
				}
			});
		});
	});
	app.post('/reviewRequest',function(req,res){
		console.log('오긴왔나');
		var tradeNum = req.body.tradeNum;
		sessionManager.sessionCheck(req,function(login,username){
			if(tradeNum===null || !tradeNum){
				console.log('여기니1');
				res.render('trade/inspectTrade',{'auth':false,'result':null});
			}else if(!login || login===false || username==='guest'){
				console.log('여기니2');
				res.render('trade/inspectTrade',{'auth':false,'result':null});
			}else{
				tradeModule.getDriverInfoWhoRequest(tradeNum,function(err,result){
					if(!err || result!==null){
						console.log('여기니3');
						console.log(result);
						res.render('trade/inspectTrade',{'auth':true,'result':result});
					}else{
						console.log('여기니4');
						res.render('trade/inspectTrade',{'auth':false,'result':null});
					}
				});
			}
		});
	});
	app.post('/tradeResponseOk',function(req,res){
		console.log('ok오긴왔나');
		var tradeNum = req.body.tradeNum;
		sessionManager.sessionCheck(req,function(login,username){
			if(tradeNum===null || !tradeNum){
				console.log('ok여기니1');
				res.json({'result':false});
			}else if(!login || login===false || username==='guest'){
				console.log('ok여기니2');
				res.json({'result':false});
			}else{
				tradeModule.setRequestInboundAccept(tradeNum,function(err){
					if(!err){
						console.log('ok여기니3');
						//console.log(result);
						res.json({'result':true});
					}else{
						console.log('ok여기니4');
						res.json({'result':false});
					}
				});
			}
		});
	});
	app.post('/tradeResponseNo',function(req,res){
		console.log('no오긴왔나');
		var tradeNum = req.body.tradeNum;
		sessionManager.sessionCheck(req,function(login,username){
			if(tradeNum===null || !tradeNum){
				console.log('no여기니1');
				res.json({'result':false});
			}else if(!login || login===false || username==='guest'){
				console.log('no여기니2');
				res.json({'result':false});
			}else{
				tradeModule.setRequestInboundDecline(tradeNum,function(err){
					if(!err){
						console.log('no여기니3');
						res.setTimeout(0);
						res.json({'result':true});
					}else{
						console.log('no여기니4');
						res.json({'result':false});
					}
				});
			}
		});
	});
	
	app.post('/getTradeDepTime',(req,res)=>
	{
		sessionManager.sessionCheck(req,(login,username)=>
		{
			if(login){
				console.log('trade num : '+req.body.tradeNum);
				tradeModule.getTradeDepTime(req.body.tradeNum,username,(err,result)=>
				{
					if(!err || result!==null){
						jsonWriter.writeTradeDepTime(result,(err2,resultJson)=>
						{
							if(!err2 || resultJson!==null){
								res.json(JSON.parse(resultJson));
								res.send();
							}else{
								console.log(err);
								res.status(204);
								res.send();
							}
						});
					}else{
						res.status(204);
						res.send();
					}
				});
			}else{
				res.status(203);
				res.send();
			}
		});
	});

	app.post('/getTradeDepListTime',(req,res)=>
	{
		sessionManager.sessionCheck(req,(login,username)=>
		{
			if(login){
				console.log('trade list : '+JSON.stringify(req.body));
				tradeModule.getTradeListDepTime(req.body.TradeNumList,username,(err,result)=>
				{
					if(!err || result!==null){
						jsonWriter.writeTradeDepListTime(result,(err2,resultJson)=>
						{
							if(!err2 || resultJson!==null){
								res.json(JSON.parse(resultJson));
								res.send();
							}else{
								console.log(err);
								res.status(204);
								res.send();
							}
						});
					}else{
						res.status(204);
						res.send();
					}
				});
			}else{
				res.status(203);
				res.send();
			}
		});
	});

};