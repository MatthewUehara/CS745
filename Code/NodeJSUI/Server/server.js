var http = require("http");
var url = require("url");
var multipart = require("./multipart/lib/multipart.js");
var sys = require("sys");
var fs = require("fs");
var path = require('path');
var qs = require('querystring');


var tool_path = "";
var tool_file_name = "fixer.jar";
var host = "java";

var port = 8080;

var useCache = false;
var cacheFileName = "cache.txt";

var server = http.createServer(function(req, res) {
    // Simple path-based request dispatcher
    
    switch (url.parse(req.url).pathname) {
        case '/':
            display_page(req, res);
            break;
        case '/fix':
            var query = url.parse(req.url,true).query;
            var id = query.id;
            upload_file(req, res, id);
            
            break;
        case '/upload':

			if (useCache)
			{
				sys.debug("Cache to be returned...");
				res.writeHead(200, { "Content-Type": "text/html" });
				var contents = fs.readFileSync(cacheFileName);
				res.end(contents);
			}
			else upload_file(req, res, 0);

            break;
        default:
            getPath(req, res);
            break;
    }
});

// Server would listen on port 8000
server.listen(port);

/*
 * Display upload form
 */
function display_page(req, res) {
    res.sendHeader(200, {"Content-Type": "text/html"});
	var contents = fs.readFileSync("./Client/app.html");
    res.write(contents);
    res.close();
}

/*
 * Create multipart parser to parse given request
 */
function parse_multipart(req) {
    var parser = multipart.parser();

    // Make parser use parsed request headers
    parser.headers = req.headers;

    // Add listeners to request, transfering data to parser

    req.addListener("data", function(chunk) {
        parser.write(chunk);
    });

    req.addListener("end", function() {
        parser.close();
    });

    return parser;
}

/*
 * Handle file upload
 */
 
function basename(str)
{
   var base = new String(str).substring(str.lastIndexOf('/') + 1); 
   base = base.replace(":", "");
//    if(base.lastIndexOf(".") != -1)       
//       base = base.substring(0, base.lastIndexOf("."));
   return base;
}
 
function upload_file(req, res, id) {
    // Request body is binary
    req.setBodyEncoding("binary");
    
    if (req.method == 'POST') {
        var body = '';
        req.on('data', function (data) {
            body += data;
        });
        req.on('end', function () {

            var POST = qs.parse(body);
            // use POST
            
            sys.debug("model" + POST.model);
            
            upload_complete(res, POST.model, id);
            return;


        });
    }
}

function escapeHtml(unsafe) {
  return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
}

function changeFileExt(name, ext, newExt)
{
	var ending = name.toLowerCase().substring(name.length - 4);
	if (ending == ext.toLowerCase())
		return name.substring(0, name.length - 4) + newExt;

	return name;
}

function upload_complete(res, contents, id) {
    sys.debug("Request complete");
	
	res.writeHead(200, { "Content-Type": "text/html" });

    if (id == 0)
    {
        arg = "-propose";
    }
    else
    {
        arg = "-fix_" + id;
    }
    
    sys.debug("Execute: " + tool_path + tool_file_name);

	var util  = require('util'),
		spawn = require('child_process').spawn,
		tool  = spawn(host, ["-jar", tool_path + tool_file_name, arg]);
	
	var error_result = "";
	var data_result = "";
	
    tool.stdin.write(contents);
    tool.stdin.end();
    
	tool.stdout.on('data', function (data) 
	{	
	  data_result += data;
	});

	tool.stderr.on('data', function (data) {
	  error_result += data;
	});

	tool.on('exit', function (code) 
	{
		var result = "";
		
		if (code === 0) 
		{
			result = data_result;
//			result = escapeHtml(result);
		}
		else 
		{
			result = 'Error, return code: ' + code + '\n' + error_result;
		}
	  
      
        sys.debug("Exec result: " + result);
	  
		res.end(result);
	  	  
	});
		
//  res.write("Hello world\n");
  
}

/*
 * Handles page not found error
 */
function show_404(req, res) {
    res.sendHeader(404, {"Content-Type": "text/html"});
    res.write("Not found!");
    res.end();
}

var extToMimes = {
	'js': 'text/javascript',
	'css': 'text/css',
	'gif': 'image/gif',
	'png': 'image/png'
}

function getMimeByExt(filename) 
{
	var ext = filename.split('.').pop();

	if (extToMimes.hasOwnProperty(ext)) {
		return extToMimes[ext];
	}
	return "text/html";
}

function getPath(req, res) {
	try
	{
		var contentType = getMimeByExt(url.parse(req.url).pathname);
		res.sendHeader(200, { "Content-Type": contentType });

		sys.debug(url.parse(req.url).pathname);
		contents = fs.readFileSync("." + url.parse(req.url).pathname);
		res.write(contents);
		res.end();
	}
	catch(e)
	{
		show_404(req, res);
	}
}