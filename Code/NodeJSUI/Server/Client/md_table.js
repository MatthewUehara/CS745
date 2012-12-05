function Table(host)
{ 
    this.id = "mdTable";
    this.title = "Proposed Fixes";

    this.width = 500;
    this.height = 350;
    this.posx = 500;
    this.posy = 150;
    
    this.host = host;
    this.content = "";
    
}

Table.method("onDataLoaded", function(data){

    if (data.verificationResult == "consistent")
    {
        this.content = "The model is consistent, no repairs.";
        return;
    }

    if (!data.fixes || data.error) // then error
    {
        this.content = data.error;    
        return;
    }
    
    var renderedData = "";

    for (var i = 0; i < data.fixes.length; i++)
    {
        var resolveLink = '<a href="#" class="resolve" id="resolve_' + data.fixes[i].id + '">Resolve</a>';       
        renderedData += '<tr><td>' + data.fixes[i].statement + '</td><td>' + data.fixes[i].est + '</td><td>' + resolveLink + '</td></tr>';
    }
    
    var sContent = '<div class="fixtable"><table id="fixtable">';
    var sData = '<tr class="header"><th>Proposed Fix</th><th>#next fixes</th><th>Resolve</th></tr>' + renderedData;    
    sContent = sContent + sData + '</table></div>';
    
    this.content = sContent;
});

Table.method("onRendered", function()
{
    $(".resolve").click(function()
    {
        var sid = this.id.split("_")[1];
        var surl = "/fix?id=" + sid;
        
        $.ajax({
            type: "POST",
            url: surl,
            cache: false,
            data: {model: getModelValue(), id: sid},
            success: function(data){
                alert(data);
            }
        });        
        
    }
    );
});

Table.method("getContent", function()
{
    return this.content;
});

Table.method("getInitContent", function()
{
    var result = "";
    
    result = 'Fixes will appear here';

	return result;	   
});