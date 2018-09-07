
module.exports = function(app,passport,jsonWriter,driverModules,sessionManager){	
	app.post('/driverInfo',function(req,res){
		var driverId = req.body.driverId ;
		driverModules.getDriverInfo(driverId,function(err,result){
			if(!err || result!==null || err===null){
				//맞을경우
				var jsonString = '{"driverId":"'+result.d_id+'","driverRank":"'+result.d_rank+'","driverMemo":"'+result.d_memo+'","driverPhoto":"'+result.d_photo+'"}';
				return res.json(jsonString);
			}else{
				return res.setTimeout(0);
			} 
		});
	});
	app.post('/requestDriverInfoLiteForAndroid',(req,res)=>{
		var driverId = req.body.driverId;
		sessionManager.sessionCheck(req,(login,username)=>{
			if(!login ||username==='guest'){
				res.status(203);
				res.send();
				return;
			}else{
				driverModules.getDriverInfoLite(driverId,(err,result)=>{
					if(err || result===null){
						console.log(err);
						res.status(204);
						res.send();
					}else{
						jsonWriter.writeDriverInfoLite(result,(err,jsonResult)=>{
							if(err || jsonResult===null){
								console.log(err);
								res.status(204);
								res.send();
							}else{
								res.json(JSON.parse(jsonResult));
								res.status(200);
								res.send();
							}
						});
					}
				});
			}
		});

	});
};