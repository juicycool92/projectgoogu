
var format = require('date-format');
var fs = require('fs');
module.exports.test = function(test){

	fs.appendFile('test.txt', format('yyyy:MM:dd-hh:mm:ss.SSS[GMT O]', new Date())+'test\n', ()=> {});
};
module.exports.loginAttemp = function(id,pw){

	fs.appendFile('test.txt','['+format('yyyy:MM:dd-hh:mm:ss.SSS[GMT O]', new Date())+'][INFO][loginAttem][id:'+id+'][pw:'+pw+']\n', ()=> {});
};
module.exports.currentMap = function(lat,lon){

	fs.appendFile('test.txt','['+format('yyyy:MM:dd-hh:mm:ss.SSS[GMT O]', new Date())+'][INFO][location][lat:'+lat+'][lon:'+lon+']\n', ()=> {});
};
module.exports.locationLog = (userId,location)=>
{
	fs.appendFile('locationLog.txt','['+format('yyyy:MM:dd-hh:mm:ss.SSS[GMT O]', new Date())+'][INFO][userId]'+userId+'[lat]'+location.lat+'[lon]'+location.lon+'/n',()=>{});
};
