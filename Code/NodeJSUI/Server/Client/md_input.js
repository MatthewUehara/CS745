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
    var options = new Object();
    options.beforeSubmit = this.beginQuery.bind(this);
    options.success = this.showResponse.bind(this);
 
    $('#myform').ajaxForm(options); 
//	$('#myform').submit();
});

Input.method("beginQuery", function(formData, jqForm, options) { 
	$("#load_area #myform").hide();
	$("#load_area").append('<div id="preloader"><img id="preloader_img" src="/Client/images/preloader.gif" alt="Loading..."/><span>Loading and processing...</span></div>');	
    return true; 
});
 
// post-submit callback 
Input.method("endQuery", function()  { 
	$("#preloader").remove();
	$("#load_area #myform").show();
	
	return true;
});

// pre-submit callback 
Input.method("showRequest", function(formData, jqForm, options) {  
    var queryString = $.param(formData); 
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
    
    var data = eval('(' + result + ')');
            
    this.host.updateData(data);
});

Input.method("getInitContent", function()
{
	var result = '<div id="load_area" style="width:100%;height:100%"><form  style="width:100%;height:100%" id="myform" action="' + this.serverAction + '" method="post">' + '<input type="submit" value="Propose">';
    
    result += '<div style="width:96%;height:90%"><textarea id="model" name="model" style="width:100%;height:100%"   ></textarea></div>';

    result += '</form></div>';
    
    return result;
});
