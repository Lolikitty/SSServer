<%-- 
    Document   : edit_file
    Created on : 2015/3/8, 上午 02:14:26
    Author     : Loli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script type="text/javascript" src="/js/jquery/jquery-1.11.2.js"></script>
        <script type="text/javascript" src="/js/tool.js"></script>
        <script type="text/javascript" src="/js/get_file_data.js"></script>        
        <script type="text/javascript"> getFileData();</script>    
        <script type="text/javascript" src="/js/save.js"></script>
        <script type="text/javascript" src="/js/run.js"></script>
    </head>
    <body>
        <!--        <pre class="code"></pre>-->
        <button onclick="save()">儲存</button>
        <button onclick="run()">執行</button>
        <br/>
        <br/>
        <textarea rows="30" cols="100" class="code" id="code"></textarea>
        <br/>
        <br/>
        輸出：
        <br/>
        <textarea rows="10" cols="100" class="output"></textarea>

    </body>
</html>
