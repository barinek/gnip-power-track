<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Gnip Power Track - Heroku Example</title>
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le styles -->
    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">
    <style type="text/css">
        body {
            padding-top: 60px;
        }
    </style>

    <script language="javascript" src="http://www.google.com/jsapi"></script>
</head>

<body>

<div class="topbar">
    <div class="fill">
        <div class="container">
            <a class="brand" href="#">City Keywords</a>
            <ul class="nav">
                <li class="active"><a href="/">Home</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="span10">
            <h2>Tweets by City Keywords Top 10</h2>

            <p>&nbsp;</p>

            <div id="keywords_by_city"></div>

            <script type="text/javascript">
                function onLoadCallback() {
                    var dataTable = new google.visualization.DataTable();
                    dataTable.addRows(10);
                    dataTable.addColumn('number');
                <#assign values = "">
                <#assign names = "">
                <#list keywordsTop10 as keyword>
                    dataTable.setValue(${keyword_index}, 0, ${keyword.getCount()?c});
                    <#assign values = values + "" + keyword.getCount()?c>
                    <#assign names = names + "" + keyword.getKeyword()>
                    <#if keyword_has_next>
                        <#assign values = values + ",">
                        <#assign names = names + "|">
                    </#if>
                </#list>

                    draw(dataTable);
                }

                function draw(dataTable) {
                    var vis = new google.visualization.ImageChart(document.getElementById('keywords_by_city'));
                    var options = {
                        chs: '600x400',
                        cht: 'p',
                        chco: '80C65A,ffffff',
                        chd: 't:${values}',
                        chdl: '${names}',
                        chl: '${names}'
                    };
                    vis.draw(dataTable, options);
                }

                google.load("visualization", "1", {packages:["imagechart"]});
                google.setOnLoadCallback(onLoadCallback);
            </script>

            <p>&nbsp;</p>

            <h2>Tweets by City Keywords</h2>

            <p>&nbsp;</p>
            <table class="zebra-striped">
            <#list keywords as keyword>
                <tr>
                    <td>${keyword.getKeyword()}</td>
                    <td>${keyword.getCount()}</td>
                    <td>${keyword.getCreatedAt()}</td>
                    <td>${keyword.getUpdatedAt()}</td>
                </tr>
            </#list>
            </table>
        </div>
    </div>

    <footer>
        <p>&copy; Company 2011</p>
    </footer>

</div>

</body>
</html>
