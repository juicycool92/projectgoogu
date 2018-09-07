var User = require('../../modules/user');
var sharp = require('sharp');
//var multiparty = require('multiparty');
var multer = require('multer');
var storage = multer.memoryStorage();
var upload = multer({storage: storage});
var fs = require('fs');
//var streams = require('memory-streams');
module.exports = function(app,pool,passport,sessionManager,locationModule,jsonWriter){	
	app.get('/user',function(req,res){
		sessionManager.sessionCheck(req,function(login,username){
			if(login){
				res.render('./login/userInfo',{'login':login,'userId':username});
			}else{
				res.render('index',{'login':login,'userId':username});
			}
		});
	});
	
	app.put('/user',function(req,res){
		//회원정보 수정 메뉴 추수 추가하자
	});
	
	app.get('/user/requestDriver',function(req,res){
		//이걸 써야 할 필요는 있을까? 아직 미정
	});
	 
	app.post('/user/requestDriver',function(req,res){
		sessionManager.sessionCheck(req,function(login,username){
			if(!login){
				res.render('index',{'login':login,'userId':username});
			}else{
				User.setPromoteDriver(username,function(err){
					if(!err){
						
						res.render('./login/userInfo',{'login':login,'userId':username});
					}else{
						res.render('index',{'login':login,'userId':username});
						//이미 드라이버라서 돌려보내는데, 메시지는 어떻게 보낼까? 좀 생각 해 보자.flash?
					}
				});
				//이제 여기서 유저가 드라이버인지 유무를 확인한다.
				//이후 드라이버 등록을 시작한다.
				//결과로, 마지막에 홈으로 리턴을 한다, message와 함께
				
			}
		});
	});
	
	app.post('/requestMyImageForAndroid',(req,res)=>{
		var userId = req.body.userId;
		User.requestMyImage(userId,(err,data)=>{
			if(!err){
				var buffer = new Buffer(data,'binary');
				res.send(buffer);
			}else{
				console.log(userId +'requestProfileImage failed err :'+err);
			}
		});
	});
	app.post('/uploadMyImageForAndroid',upload.single('image'),(req,res)=>{
		console.log(req.file);
		sharp(req.file.buffer).resize(150,150).toBuffer()
		.then(data =>{
			User.updateImagesForUser(req.file.buffer,data,req.body.userId,(err)=>{
				if(!err){
					res.send();
				}else{
					res.statusCode=404;
				}
			});
		}).catch(err =>{
			console.log(req.body.userId+' upload image error :'+err);
		});
	});
	
	app.post('/uploadTest',function(req,res){
		var form = new multiparty.Form();
		var userId ;
		form.on('field',function(name,value){
			console.log('normal field / name'+name+', value /'+value);
			userId = value;
		});
		form.on('part',function(part){
			var filename;
			var size;
			if(part.filename){
				filename = '['+Date.now()+']['+userId+']'+part.filename;
				size = part.size;
				
			}else{
				part.resume();
			}
			console.log('writing streamming file '+filename);
			var writer = new streams.WritableStream();
            part.pipe(writer);
	        part.on('data',function(chunk){
	              console.log(filename+' read '+chunk.length + 'bytes');
	        });
	        part.on('end',function(){
	        console.log(' Part read complete');
	             var reader = new streams.ReadableStream(part);
	             User.updateImagesForUser(writer.toBuffer(),null,userId,function(err){
	            	 if(!err){
	            		 console.log('yaho');
	            	 }
	            	 else{
	            		 console.log(err);
	            	 }	
	             });  
	        });
		});
		form.on('close',function(){
			res.send();
	    });
	    // track progress
	    form.on('progress',function(byteRead,byteExpected){
	    	console.log(' Reading total  '+byteRead+'/'+byteExpected);
	    });
	    form.parse(req);
	});
	app.post('/updateMyLocForDepTime',(req,res)=>{
		
		var location = req.body.myLocationPojo;
		var tradeList =  req.body.tradeNumlist;
		console.log('loc'+location);
		console.log('loc lat '+location.lat);
		console.log('loc lon '+location.lon);
		console.log('hey len '+tradeList.length);
		console.log('hey [0] '+tradeList[0]);
		console.log('json '+JSON.stringify(req.body));
		sessionManager.sessionCheck(req,(login,username)=>{
			if(!login){
				res.status(203);
				res.send();
			}else{
				locationModule.updateMyLocatation(username,location,(err)=>{
					if(err){
						res.status(204);
						res.send();
					}else{
						locationModule.updateListTradeLocation(tradeList,(err,result)=>{
							if(err || result.length === 0){
								console.log(err);
								res.status(204);
								res.send();
							}else{
								//제이슨으로 쓰고 보내자
								jsonWriter.writeTradeListLocation(result,(err,jsonResult)=>{
									if(err || jsonResult === null){
										console.log(err);
										res.status(204);
										res.send();
									}else{
										console.log(JSON.parse(jsonResult));
										res.json(JSON.parse(jsonResult));
										res.send();
									}
								});
							}
						});
					}
				});
			}
		});
		//console.log(lat+' / '+lon);
		//console.log(JSON.stringify(tradeList));
		//console.log(tradeList[0]);
		//이제 이다음에 해야할 일은 무엇인가?
		// 유저 인증 받고, er 204
		// 유저 위치를 업데이트 한다
		// 다음 tradeList에 해당하는 driverLocation을 모두 불러온다.
		// 결과를 json으로 돌라준다.
	});
};