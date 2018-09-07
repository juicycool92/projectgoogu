//dose it really need?

var User = require('../../modules/user');
var sharp = require('sharp');
var multiparty = require('multiparty');
var fs = require('fs');

module.exports = function(app,pool,passport,sessionManager){	
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
			 var writeStream = fs.createWriteStream('/home/jayubuntuserver/node/Server04/tmpFiles/'+filename);

	           writeStream.filename = filename;

	           part.pipe(writeStream);

	 

	           part.on('data',function(chunk){

	                 console.log(filename+' read '+chunk.length + 'bytes');

	           });

	          

	           part.on('end',function(){

	                 console.log(filename+' Part read complete');

	                 writeStream.end();
	                 var resizedImg = sharp('/home/jayubuntuserver/node/Server04/tmpFiles/'+filename)
	                 .resize(50)
	                 .toFile('th_'+filename);
	                 fs.writeFile('/home/jayubuntuserver/node/Server04/tmpFiles/thumbnail/th_'+filename, resizedImg, function(err){
	                	 if(err)
	                		 console.log('err :'+err);
	                	 else
	                		 console.log('done');
	                	 });
	           });
	                 

			
		});
		 form.on('close',function(){

	           res.status(200).send('Upload complete');

	      });

	     

	      // track progress

	      form.on('progress',function(byteRead,byteExpected){

	           console.log(' Reading total  '+byteRead+'/'+byteExpected);

	      });

	     

	      form.parse(req);


	});
	
};