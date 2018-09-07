

module.exports = function(app,passport,sessionManager,tradeModule,chatModule){	
	app.get('/chat',function(req,res){
		
	});
	app.post('/chat',function(req,res){
		var tradeNum = req.body.tradeNum;
		var tradeStatus = req.body.tradeStatus;
		tradeModule.getTradeStatus(tradeNum, function(err, tradeStatus) {
			if(!err || tradeStatus!==null){
				chatModule.getChatHistory(tradeNum);
			}else{
				res.render('/chat',{"status":false});
			}
		});
	});
	
};