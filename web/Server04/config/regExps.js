function isIdDuped(checkString,callback)
{
    let regExps = new RegExp('\(u_id_UNIQUE:)+','g');
    if(regExps.test(checkString)){
        callback(true)
    }else{
        callback(null);
    }
};
function isPhoneDuped(checkString,callback)
{
    let regExps = new RegExp('\(u_phone_UNIQUE)+','g');
    if(regExps.test(checkString)){
        callback(true)
    }else{
        callback(null);
    }
};
module.exports.dupEntryChecker = (checkString,callback)=>
{
    let regExps = new RegExp('\(ER_DUP_ENTRY)+','g');
    if(regExps.test(checkString)){
        isIdDuped(checkString,(isduped)=>
        {
            if(!isduped){
                isPhoneDuped(checkString,(isDuped2)=>
                {
                    if(!isDuped2){
                        callback(3);
                    }else{
                        callback(2);
                    }
                });
            }else{
                callback(1);
            }
        })
    }else{
        callback(null);
    }
}

