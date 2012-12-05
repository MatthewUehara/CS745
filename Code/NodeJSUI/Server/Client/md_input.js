function Input(host)
{ 
    this.id = "mdInput";
    this.title = "Alloy Model";

    this.width = 500;
    this.height = 500;
    this.posx = 0;
    this.posy = 0;

    this.host = host;
    this.serverAction = "/upload";
}

Input.method("onDataLoaded", function(data){
});

Input.method("onInitRendered", function()
{
    $('#propose').click(function(){        
        var surl = "/upload";
        
        var sData = "model=" + getModelValue();
        
        host.findModule("mdInput").beginQuery();
        
        $.ajax({
            type: "POST",
            url: surl,
            cache: false,
            data: sData,
            success: function(data){
                host.findModule("mdInput").processToolResult(data);    
                host.findModule("mdInput").endQuery();
            }
        });        
                
    }); 
});

Input.method("beginQuery", function(formData, jqForm, options) { 
	$("#model_container").hide();
	$("#load_area").append('<div id="preloader"><img id="preloader_img" src="/Client/images/preloader.gif" alt="Loading..."/><span>Loading and processing...</span></div>');	
    return true; 
});
 
// post-submit callback 
Input.method("endQuery", function()  { 
	$("#preloader").remove();
	$("#model_container").show();
	
	return true;
});
 
// post-submit callback 
Input.method("showResponse", function(responseText, statusText, xhr, $form)  { 
    this.processToolResult(responseText);    
	this.endQuery();
});

Input.method("processToolResult", function(result)
{
	if (!result) return;
    
//    alert(result);
    
    var data;
    
    try{
    
        data = eval('(' + result + ')');

    }
    catch(e)
    {
        data = new Object();
        data.error = "Error evaluating result";
    }
           
    this.host.updateData(data);
});

Input.method("getInitContent", function()
{
	var result = '<div id="load_area" style="width:100%;height:100%"><button id="propose">Propose</button>';
    
    result += '<div id="model_container" style="width:96%;height:90%"><textarea spellcheck="false" id="model" name="model"  wrap="off" style="width:100%;height:100%"   ></textarea></div></div>';
    
    return result;
});
