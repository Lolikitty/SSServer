<%-- 
    Document   : index
    Created on : 2015/3/7, 下午 10:43:17
    Author     : Loli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script type="text/javascript" src="/js/jquery/jquery-1.11.2.js"></script>
        <script type="text/javascript" src="/js/create_file.js"></script>
        <script type="text/javascript" src="/js/get_files.js"></script>
        <script type="text/javascript" src="/js/init.js"></script>
        <script type="text/javascript"> init();</script>
    </head>
    <body>
        <!--==============================================-->
        Cerate 
        <select id="format">
            <option value="java">Java</option>
            <option value="servlet">Servlet</option>
            <option value="html">Html</option>
        </select>

        <br/>

        File Name
        <input id="name" type="text" />
        <button onclick="create()"> 建立 </button>

        <br/>
        <br/>

        <table class="file_list" border="1" width="500"></table>


        <!--==============================================-->
    </body>
</html>



