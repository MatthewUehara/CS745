function Verification(host)
{ 
    this.id = "mdVerification";
    this.title = "Verification Result";

    this.width = 500;
    this.height = 105;
    this.posx = 500;
    this.posy = 0;
    
    this.host = host;
    this.content = "";
    
}

Verification.method("onDataLoaded", function(data){

    if (data.error) // error
    {
        this.content = data.error;
        return;
    }
    
    if (data.verificationResult == "consistent")
    {
        this.content = "The model is consistent, no repairs.";
    }
    else
    {
        this.content = "The model is inconsistent." + "<br/>" + data.verificationText;    
    }
});

Verification.method("onRendered", function()
{
});

Verification.method("getContent", function()
{
    return this.content;
});

Verification.method("getInitContent", function()
{
    var result = "";
    
    result = 'Verification results appear here';

	return result;	   
});