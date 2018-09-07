function getMyTradeArray(tradeRawFile,callback){
	var result = '';
	if(tradeRawFile.length===1){
		result +='{"tradeNum":"'+tradeRawFile[0].t_num+'",';
		result +='"tradeName":"'+tradeRawFile[0].t_goods_name+'",';
		result +='"tradeState":"'+tradeRawFile[0].t_state+'"}';
	}else{
		for(var i = 0 ; i < tradeRawFile.length-1 ; i++){
			console.log('looping json'+i);
			result +='{"tradeNum":"'+tradeRawFile[i].t_num+'",';
			result +='"tradeName":"'+tradeRawFile[i].t_goods_name+'",';
			result +='"tradeState":"'+tradeRawFile[i].t_state+'"},';
			
		}	
		result +='{"tradeNum":"'+tradeRawFile[tradeRawFile.length-1].t_num+'",';
		result +='"tradeName":"'+tradeRawFile[tradeRawFile.length-1].t_goods_name+'",';
		result +='"tradeState":"'+tradeRawFile[tradeRawFile.length-1].t_state+'"}';
	}
	callback(null,result);
}
function getMyActiveTrade(activeTradeRawFile,callback){
	var results = '';
	
	switch(activeTradeRawFile.length){
		case 1: {
			results = '{"tradeNum" :"'+activeTradeRawFile[0].t_num+'", "tradeStatus" :"'+activeTradeRawFile[0].t_state+'","itemDepTime" :"'+activeTradeRawFile[0].t_departure_time+'"}';
			callback(null,results);
			break;
		}
		default : { 
			for(var i = 0 ; i < activeTradeRawFile.length-1; i++){
				results += '{"tradeNum" :"'+activeTradeRawFile[i].t_num+'", "tradeStatus" :"'+activeTradeRawFile[i].t_state+'","itemDepTime" :"'+activeTradeRawFile[i].t_departure_time+'"},';
			}
			results += '{"tradeNum" :"'+activeTradeRawFile[activeTradeRawFile.length-1].t_num+'", "tradeStatus" :"'+activeTradeRawFile[activeTradeRawFile.length-1].t_state+'","itemDepTime" :"'+activeTradeRawFile[activeTradeRawFile.length-1].t_departure_time+'"}';
			callback(null,results);
		}
	}
}
function getMyTradeDriver(tradeRawFile,callback){
	var results = '';
	
	switch(tradeRawFile.length){
		case 1: {
			console.log(typeof tradeRawFile[0].ul_gps_lat);
			console.log(typeof tradeRawFile[0].ul_gps_lon);
			results = '{"driverLat" :"'+tradeRawFile[0].ul_gps_lat+'", "driverLon" :"'+tradeRawFile[0].ul_gps_lon+'","driverId" :"'+tradeRawFile[0].ul_id+'","tradeNum" :"'+tradeRawFile[0].t_num+'", "tradeName" :"'+tradeRawFile[0].t_goods_name+'"}';
			callback(null,results);
			break;
		}
		default : { 
			for(var i = 0 ; i < tradeRawFile.length-1; i++){
				results += '{"driverLat" :"'+tradeRawFile[i].ul_gps_lat+'", "driverLon" :"'+tradeRawFile[i].ul_gps_lon+'","driverId" :"'+tradeRawFile[i]+'","tradeNum" :"'+tradeRawFile[i].t_num+'", "tradeName" :"'+tradeRawFile[i].t_goods_name+'"},';
			}
			results += '{"driverLat" :"'+tradeRawFile[tradeRawFile.length-1].ul_gps_lat+'", "driverLon" :"'+tradeRawFile[tradeRawFile.length-1].ul_gps_lon+'","driverId" :"'+tradeRawFile[tradeRawFile.length-1].ul_id+'","tradeNum" :"'+tradeRawFile[tradeRawFile.length-1].t_num+'", "tradeName" :"'+tradeRawFile[tradeRawFile.length-1].t_goods_name+'"}';
			callback(null,results);
		}
	}
	
}
function getNearTradeArray(rows,callback){
	
	var results = '';
	
	switch(rows.length){
		case 1: {
			results = '{"lat" :"'+rows[0].t_departure_place_lat+'", "lon" :"'+rows[0].t_departure_place_lon+'", "itemName" :"'+rows[0].t_goods_name.toString()+'","itemPrice":"'+rows[0].t_cost.toString()+'","itemDepTime":"'+rows[0].t_departure_time.toString()+'","itemArrPlace":"'+rows[0].t_arrival_place.toString()+'", "tradeNum" : "'+rows[0].t_num.toString()+'"}';
			callback(null,results);
			break;
		}
		default : { 
			for(var i = 0 ; i < rows.length-1; i++){
				results += '{"lat" :"'+rows[i].t_departure_place_lat+'", "lon" :"'+rows[i].t_departure_place_lon+'", "itemName" :"'+rows[i].t_goods_name+'","itemPrice":"'+rows[i].t_cost+'","itemDepTime":"'+rows[i].t_departure_time+'","itemArrPlace":"'+rows[i].t_arrival_place+'", "tradeNum" : "'+rows[i].t_num+'"},';
			}
			results += '{"lat" :"'+rows[rows.length-1].t_departure_place_lat+'", "lon" :"'+rows[rows.length-1].t_departure_place_lon+'", "itemName" :"'+rows[rows.length-1].t_goods_name+'","itemPrice":"'+rows[rows.length-1].t_cost+'","itemDepTime":"'+rows[rows.length-1].t_departure_time+'","itemArrPlace":"'+rows[rows.length-1].t_arrival_place+'", "tradeNum" : "'+rows[rows.length-1].t_num+'"}';
			callback(null,results);
		}
	}
}

module.exports.writeTradeDepListTime = function(tradeDepRaw, callback)
{
	if(!tradeDepRaw || !tradeDepRaw.length || tradeDepRaw[0].tradeNum === null){
		console.log('no tradeDepRaw! '+tradeDepRaw);
		callback('no tradeDepRaw!',null);
		return;
	}
	var jsonStrings =  '{"tradeDepPojo" : [';
	switch(tradeDepRaw.length){
		case 0 : {
			console.log('no tradeDepRaw! '+tradeDepRaw);
			callback('no tradeDepRaw!',null);
			return;
		}
		case 1 : {
			jsonStrings += '{"tradeNum":"'+tradeDepRaw[0].tradeNum+'","tradeDepTime":"'+tradeDepRaw[0].tradeDepTime+'"}]}';
			break;
		}
		default : {
			for(var i = 0 ; i < tradeDepRaw.length-1 ; i++){
				jsonStrings += '{"tradeNum" : "'+tradeDepRaw[i].tradeNum+'", "tradeDepTime" : "'+tradeDepRaw[i].tradeDepTime+'" },';
			}
			jsonStrings += '{"tradeNum" : "'+tradeDepRaw[tradeDepRaw.length-1].tradeNum+'", "tradeDepTime" : "'+tradeDepRaw[tradeDepRaw.length-1].tradeDepTime+'" }]}';
			break;
		}
	}
	console.log('write json successfully '+jsonStrings);
	callback(null,jsonStrings);
	return;
}

module.exports.writeTradeDepTime = function(tradeDepRaw, callback)
{
	if(!tradeDepRaw){
		console.log('no tradeDepRaw! '+tradeDepRaw);
		callback('no tradeDepRaw!',null);
		return;
	}
	var jsonStrings = '{"tradeNum":"'+tradeDepRaw.tradeNum+'","tradeDepTime":"'+tradeDepRaw.tradeDepTime+'"}';
	callback(null,jsonStrings);
	console.log('write json successfully '+jsonStrings);
}

module.exports.createJsonforAllandMyTradeDriver = function(nearTradeRaw,myTradeDriverRaw,callback){
	var jsonStrings = '{ "nearTradeArr": [';
	if(nearTradeRaw!==null){
		getNearTradeArray(nearTradeRaw,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += '], "myActiveTradeDriver":[';
	if(myTradeDriverRaw!==null){
		getMyActiveTradeDriver(myTradeDriverRaw,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
    jsonStrings += ']}';
	//console.log(jsonStrings);
	callback(null,jsonStrings);

}
function getMyActiveTradeDriver(myTradeDriverRaw,callback){
	
	
	var results = '';
	
	switch(myTradeDriverRaw.length){
		case 1: {
			var buffer = new Buffer(myTradeDriverRaw[0].driver_t_profile,'binary');
			var incode = buffer.toString('base64');
			
			results = '{"tradeNum" :"'+myTradeDriverRaw[0].tradeNum+'","tradeStatus":"'+myTradeDriverRaw[0].tradeStatus+'", "driverId" :"'+myTradeDriverRaw[0].driverId+'","driverTprofile" :"'+incode+'","driverLat" :"'+myTradeDriverRaw[0].driverLat+'", "driverLon" :"'+myTradeDriverRaw[0].driverLon+'"}';
			callback(null,results);
			break;
		}
		default : { 
			for(var i = 0 ; i < myTradeDriverRaw.length-1; i++){
				
				let buffer = new Buffer(myTradeDriverRaw[i].driver_t_profile,'binary');
				let incode = buffer.toString('base64');
				results += '{"tradeNum" :"'+myTradeDriverRaw[i].tradeNum+'", "tradeStatus":"'+myTradeDriverRaw[i].tradeStatus+'", "driverId" :"'+myTradeDriverRaw[i].driverId+'","driverTprofile" :"'+incode+'","driverLat" :"'+myTradeDriverRaw[i].driverLat+'", "driverLon" :"'+myTradeDriverRaw[i].driverLon+'"},';
			}
			let buffer = new Buffer(myTradeDriverRaw[myTradeDriverRaw.length-1].driver_t_profile,'binary');
			var incode = buffer.toString('base64');
			results += '{"tradeNum" :"'+myTradeDriverRaw[myTradeDriverRaw.length-1].tradeNum+'", "tradeStatus":"'+myTradeDriverRaw[myTradeDriverRaw.length-1].tradeStatus+'", "driverId" :"'+myTradeDriverRaw[myTradeDriverRaw.length-1].driverId+'","driverTprofile" :"'+incode+'","driverLat" :"'+myTradeDriverRaw[myTradeDriverRaw.length-1].driverLat+'", "driverLon" :"'+myTradeDriverRaw[myTradeDriverRaw.length-1].driverLon+'"}';
			callback(null,results);
		}
	}
};
module.exports.writeTradeListLocation = function(tradeLocListRaw,callback)
{
	var jsonString = '{"tradeLocList" : [';
	switch(tradeLocListRaw.length){
		case 1:{
			jsonString += '{"tradeNum":"'+tradeLocListRaw[0].tradeNum+'","tradeStatus":"'+tradeLocListRaw[0].tradeStatus+'","lat":"'+tradeLocListRaw[0].lat+'","lon":"'+tradeLocListRaw[0].lon+'"}';
			break;
		}
		default : {
			for(var i = 0 ; i < tradeLocListRaw.length-1 ; i++){
				jsonString += '{"tradeNum":"'+tradeLocListRaw[i].tradeNum+'","tradeStatus":"'+tradeLocListRaw[i].tradeStatus+'","lat":"'+tradeLocListRaw[i].lat+'","lon":"'+tradeLocListRaw[i].lon+'"},';
			}
			jsonString += '{"tradeNum":"'+tradeLocListRaw[tradeLocListRaw.length-1].tradeNum+'","tradeStatus":"'+tradeLocListRaw[tradeLocListRaw.length-1].tradeStatus+'","lat":"'+tradeLocListRaw[tradeLocListRaw.length-1].lat+'","lon":"'+tradeLocListRaw[tradeLocListRaw.length-1].lon+'"}';
			break;
		}
	}
	jsonString += ']}';
	console.log('json created like : '+jsonString);
	callback(null,jsonString);
}
module.exports.writeDriverInfoLite = function(driverInfo,callback){
	var buffer = new Buffer(driverInfo.driverPic,'binary');
	var incode = buffer.toString('base64');
	var jsonStrings='';
	try{
		jsonStrings = '{"driverName" : "'+driverInfo.driverName+'", "driverPic" : "'+incode+'", "driverRank" : "'+driverInfo.driverRank+'", "driverContext":"'+driverInfo.driverContext+'"}';
	}catch(exception){
		callback(exception,null);
	}
	callback(null,jsonStrings);
};
module.exports.writeForMyTradeList = function(tradeRawFile,callback){
	var jsonStrings = '{ "trade": [';
	if(tradeRawFile!==null){
		getMyTradeArray(tradeRawFile,function(err,strings){
			jsonStrings+=strings;
		});
		jsonStrings += ']}';
		console.log('if sab : '+jsonStrings);
		callback(null,jsonStrings);
	}else{
		jsonStrings += ']}';
		console.log('else sab : '+jsonStrings);
		callback(null,jsonStrings);
	}
	
	
};
module.exports.createJsonforAllandMyTradeActiveTradeDriverList= function(allTrade,myActiveTrade,myTradeDriver,callback){
	var jsonStrings = '{ "nearTradeArr": [';
	if(allTrade!==null){
		getNearTradeArray(allTrade,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += '], "myActiveTradeArr":[';
	if(myTradeDriver!==null){
		getMyActiveTrade(myActiveTrade,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += '], "driverArray":[';
	if(myTradeDriver!==null){
		getMyTradeDriver(myTradeDriver,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += ']}';
	console.log(jsonStrings);
	callback(null,jsonStrings);
}
module.exports.createJsonforAllandMyTradeDriverList = function(allTrade,myTradeDriver,callback){
	var jsonStrings = '{ "nearTradeArr": [';
	if(allTrade!==null){
		getNearTradeArray(allTrade,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += '], "driverArray":[';
	if(myTradeDriver!==null){
		getMyTradeDriver(myTradeDriver,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += ']}';
	//console.log(jsonStrings);
	callback(null,jsonStrings);
};
/*
module.exports.creteJsonforAllandMyTradeList = function(allTrade,myTrade,callback){
	var jsonStrings = '{ "nearTradeArr": [';
	if(allTrade!==null){
		getNearTradeArray(allTrade,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += '], "myTradeArr":[';
	if(myTrade!==null){
		getMyTradeArray(myTrade,function(err,strings){
			if(!err){
				jsonStrings+=strings;
			}
			else{
		
			}
		});
	}
	jsonStrings += ']}';
	callback(null,jsonStrings);
}; */
module.exports.createJsonforTradeDetail = function(rows,callback){
	var jsonString = '{';
	jsonString+='"itemName":"'+rows.t_goods_name+'",';
	jsonString+='"itemCost":"'+rows.t_cost+'",';
	jsonString+='"itemSize":"'+rows.t_size+'",';
	jsonString+='"itemWeight":"'+rows.t_quantity+'",';
	jsonString+='"itemMemo":"'+rows.t_notice+'",';
	jsonString+='"itemSender_name":"'+rows.t_sender_name+'",';
	jsonString+='"itemSender_lat":"'+rows.t_departure_place_lat+'",';
	jsonString+='"itemSender_lon":"'+rows.t_departure_place_lon+'",';
	jsonString+='"itemSender_time":"'+rows.t_departure_time+'",';
	jsonString+='"itemReceiver_lat":"'+rows.t_arrival_place_lat+'",';
	jsonString+='"itemReceiver_lon":"'+rows.t_arrival_place_lon+'",';
	jsonString+='"itemReceiver_time":"'+rows.t_arrival_time+'"}';
	callback(jsonString);
};
/*
module.exports.createJsonforRefreshMap = function(rows,callback){
	var jsonStrings = '{ "nearTradeArr": [';
	getNearTradeArray(rows,function(err,strings){
		if(!err){
			jsonStrings+=strings;
		}
		else{
	
		}
	});
	
	jsonStrings += ']}';
	console.log(jsonStrings);
	callback(null,jsonStrings);
};
*/