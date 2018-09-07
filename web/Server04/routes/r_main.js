module.exports = function(app, fs, connection, jsonWriter, mainModule,
		sessionManager) {

	app.get('/', function(request, response, next) {
		sessionManager.sessionCheck(request, function(login, username) {
			response.render('index', {
				'login' : login,
				'userId' : username
			});
		});
		
		
	});
	app.post('/testTrade', function(req, res) {
		mainModule.getTestFunction(req.body.tradeNum,(err,result)=>{
			
			if(err){
				res.send('err accure'+err);
			}
			
			else{
				//res.send(Number.parse(result));
				console.log(result);
				//console.log(Json.parse(result));
				console.log(Date.parse(result));
				//console.log(Number.parse(result));
				//console.log(String.parse(result));
			}
				

		});
	});
	app.get('/testForAndroid',function(req,res){
		console.log('start test route id['+req.query.userId+']');
		res.json({"userId":req.query.userId,"userPw":"secret","userLocation":[{"lat":"333","lon":"129"}]});
		res.send();
	});
	app.post('/mapReqForAndroid',function(req,res){

		var locationLat = req.body.locationLat;
		var locationLon = req.body.locationLon;
		
		var reqUserId;
		var zoom = req.body.zoom;
		var allTradeListArr = [];
		var myTradeListArr = [];
		sessionManager.sessionCheck(req,(login,username)=>
		{
			reqUserId = username;
		});
		console.log('mapReqForAndroid income data -> \n userid[',reqUserId,'] \nuserLat[',locationLat,']\nuserLon[',locationLon,']\n----endreq---');
		mainModule.onLoadAllTradeList(locationLat, locationLon, zoom,reqUserId, function(err1, tradeListAll) {
			// tradeList뽑았고, 이제 내 거래상태창도 띄워보자.
			//여기에 새로 추가될 활동중인 거래내역 뽑기 함수를 하나 더 만들자
			mainModule.onLoadMyTradeDriverList(reqUserId,function(err2,myTradeDriverList){
				if(err1 || err2){
					res.setTimeout(0);
					return null;
				}else{
					jsonWriter.createJsonforAllandMyTradeDriver(tradeListAll,myTradeDriverList,(err,jsonString)=>{
						if(err){
							res.setTimeout(0);
							return null;
						}else if(!jsonString){
							res.setTimeout(0);
							return null;
						}else{
							res.json(JSON.parse(jsonString));
							res.send();
						}
					});
				};
			});
		});
	});

	app.post('/', function(req, res) {
		var location = req.body.location;
		var reqUserId = req.body.userId;
		var zoom = req.body.zoom;
		var allTradeListArr = [];
		var myTradeListArr = [];
		console.log('location :'+location.lat +' / '+ location.lon );
		
		mainModule.onLoadAllTradeList(location.lat, location.lon, zoom,function(err1, tradeListAll) {
			// tradeList뽑았고, 이제 내 거래상태창도 띄워보자.
			console.log('all드르가'); 
			mainModule.onLoadMyTradeDriver(reqUserId, function(err2,tradeListMy) {
				// 내 거래 리스트
				console.log('my드르가');
				if (err1 || err2) {
					// 에러 발생시
					console.log('여기니1');
					res.setTimeout(0);
					return null;
				} else if (tradeListAll !== null || tradeListMy !== null) {
					//console.log(tradeListMy[0]);
					jsonWriter.createJsonforAllandMyTradeDriverList(tradeListAll, tradeListMy, function(err,jsonString) {
						if (err) {
							console.log('여기니2');
							res.setTimeout(0);
							return null;
						} else if (jsonString !== null) {
							console.log('여기니3');
							console.log(jsonString);
							res.json(jsonString);
							res.send();
						} else {
							console.log('여기니4');
							res.setTimeout(0);
							return null;
						}
					});
					// 전체거래목록과, 내 거래목록중 하나, 혹은 둘다 있을 때에
				} else {
					// 전체거래 목록과 내 거래목록 둘다 비었을때
					console.log('여기니5');
					res.setTimeout(1);
					return null;
				}
			});
		});

		mainModule.isDriver(reqUserId, function(err) {
			if (!err) {
				mainModule.updateDriverLocation(reqUserId, location.lat,location.lon);
			}
		}); // 드라이버인지 검색 후, 맞으면 로케이션 업데이트
	});
	
};