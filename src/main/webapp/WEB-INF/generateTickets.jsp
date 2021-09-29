<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> <!--输出,条件,迭代标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="fmt"%> <!--数据格式化标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="sql"%> <!--数据库相关标签库-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="fn"%> <!--常用函数标签库-->
<%@ page isELIgnored="false"%> <!--支持EL表达式，不设的话，EL表达式不会解析-->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta id="viewport" name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
    <meta name="apple-mobile-web-app-capable" content="yes"/>

</head>
<body>
<div class="nonsupport-lable" id="nonsupportDiv">
    <div>
        <form>
            <table>
                <tr>
                    <th>当前期数：</th>
                    <td>
                        <input type="text" id="issueno" name="issueno" value="${issueno}" disabled>
                    </td>
                </tr>
                <tr>
                    <th>需要查询的号码：</th>
                    <td>
                        <input type="text" id="hongQiu" name="hongQiu">
                    </td>
                </tr>
                <tr>
                    <th>号码位置：</th>
                    <td>
                        <input type="text" id="weiZhi" name="weiZhi">
                    </td>
                </tr>
            </table>
            <button type="button" onclick="queryHistory()">查询历史号码</button>
        </form>
        <div id="historyDiv">

        </div>
    </div>
    <form>
        <table>
            <tr>
                <th>中奖号码：</th>
                <td>
                    <input type="text" id="zjhm" name="zjhm" >
                </td>
            </tr>
            <tr>
                <th>生成条数：</th>
                <td>
                    <input type="text" id="num" name="num" >
                </td>
            </tr>
            <tr>
                <th>尾和最小值：</th>
                <td>
                    <input type="text" id="minWh" name="minWh" >
                </td>
            </tr>
            <tr>
                <th>尾和最大值：</th>
                <td>
                    <input type="text" id="maxWh" name="maxWh" >
                </td>
            </tr>
            <tr>
                <th>总和最小值：</th>
                <td>
                    <input type="text" id="totalMinWh" name="totalMinWh" >
                </td>
            </tr>
            <tr>
                <th>总和最大值：</th>
                <td>
                    <input type="text" id="totalMaxWh" name="totalMaxWh" >
                </td>
            </tr>
            <tr>
                <th>大小比：</th>
                <td>
                    <input type="text" id="dxb" name="dxb" >
                </td>
            </tr>
            <tr>
                <th>单双比：</th>
                <td>
                    <input type="text" id="dsb" name="dsb" >
                </td>
            </tr>
            <tr>
                <th>012第一位：</th>
                <td>
                    <input type="text" id="luShu1" name="luShu1" >
                </td>
            </tr>
            <tr>
                <th>012第二位：</th>
                <td>
                    <input type="text" id="luShu2" name="luShu2" >
                </td>
            </tr>
            <tr>
                <th>012第三位：</th>
                <td>
                    <input type="text" id="luShu3" name="luShu3" >
                </td>
            </tr>
            <tr>
                <th>012第四位：</th>
                <td>
                    <input type="text" id="luShu4" name="luShu4" >
                </td>
            </tr>
            <tr>
                <th>012第五位：</th>
                <td>
                    <input type="text" id="luShu5" name="luShu5" >
                </td>
            </tr>
            <tr>
                <th>012第六位：</th>
                <td>
                    <input type="text" id="luShu6" name="luShu6" >
                </td>
            </tr>
            <tr>
                <th>单双第一位：</th>
                <td>
                    <input type="text" id="ds1" name="ds1" >
                </td>
            </tr>
            <tr>
                <th>单双第二位：</th>
                <td>
                    <input type="text" id="ds2" name="ds2" >
                </td>
            </tr>
            <tr>
                <th>单双第三位：</th>
                <td>
                    <input type="text" id="ds3" name="ds3" >
                </td>
            </tr>
            <tr>
                <th>单双第四位：</th>
                <td>
                    <input type="text" id="ds4" name="ds4" >
                </td>
            </tr>
            <tr>
                <th>单双第五位：</th>
                <td>
                    <input type="text" id="ds5" name="ds5" >
                </td>
            </tr>
            <tr>
                <th>单双第六位：</th>
                <td>
                    <input type="text" id="ds6" name="ds6" >
                </td>
            </tr>
            <tr>
                <th>精选号码第一位：</th>
                <td>
                    <input type="text" id="jx1" name="jx1" >
                </td>
            </tr>
            <tr>
                <th>精选号码第二位：</th>
                <td>
                    <input type="text" id="jx2" name="jx2" >
                </td>
            </tr>
            <tr>
                <th>精选号码第三位：</th>
                <td>
                    <input type="text" id="jx3" name="jx3" >
                </td>
            </tr>
            <tr>
                <th>精选号码第四位：</th>
                <td>
                    <input type="text" id="jx4" name="jx4" >
                </td>
            </tr>
            <tr>
                <th>精选号码第五位：</th>
                <td>
                    <input type="text" id="jx5" name="jx5" >
                </td>
            </tr>
            <tr>
                <th>精选号码第六位：</th>
                <td>
                    <input type="text" id="jx6" name="jx6" >
                </td>
            </tr>
        </tr>
        </table>
        <button type="button" onclick="regist()">生成号码</button>
    </form>
</div>
<div id="showData">
</div>
</body>
<script type="text/javascript" src="http://127.0.0.1:8080/static/jquery/jquery-2.1.4.min.js"></script>
<script>
    function regist() {
        var num = $("#num").val();
        if (num > 1000 || objectIsNull(num)) {
            alert("生成条数不能大于1000")
            return;
        }
        var parameters = getRequestParam("nonsupportDiv");
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/admin/generate/generate.html",
            dataType: "json",
            data: parameters,
            success: function (data) {
                if (data.respCode == "0000") {
                    $("#showData").empty();
                    var html = '<table>'
                    html+='<tr>';
                    html+='<th>尾和：</th>';
                    html+='<th>'+data.data.whNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>总和：</th>';
                    html+='<th>'+data.data.totalNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>大小比总和：</th>';
                    html+='<th>'+data.data.dxbNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>单双比总和：</th>';
                    html+='<th>'+data.data.dsbNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>012路总和：</th>';
                    html+='<th>'+data.data.luShuNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>单双总和：</th>';
                    html+='<th>'+data.data.dsNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>精选总和：</th>';
                    html+='<th>'+data.data.jxNum+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>生成的球：</th>';
                    html+='<th>'+data.data.qiuJson+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>是否包含中奖号码：</th>';
                    html+='<th>'+data.data.isSfbhzj+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>中奖信息：</th>';
                    html+='<th>'+data.data.zjxx+'</th>';
                    html+='</tr>';
                    html+='</table>';
                    $("#showData").append(html);
                } else {
                    alert(data.respMsg);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown);
            }
        });
    }

    function queryHistory() {
        var hongQiu = $("#hongQiu").val();
        var weiZhi = $("#weiZhi").val();
        if (objectIsNull(weiZhi) || objectIsNull(hongQiu)) {
            return;
        }
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/admin/generate/queryHistory.html",
            dataType: "json",
            data: {"hongQiu":hongQiu,"weiZhi":weiZhi},
            success: function (data) {
                if (data.respCode == "0000") {
                    if (objectIsNull(data.data)) {
                        return;
                    }
                    $("#historyDiv").empty();
                    var html = '<table>'
                    html+='<tr>';
                    html+='<th>历史号码最近的最前面：</th>';
                    html+='<th>'+data.data.hongQiu+'</th>';
                    html+='</tr>';
                    html+='<tr>';
                    html+='<th>历史号码余数最近的最前面：</th>';
                    html+='<th>'+data.data.yuShu+'</th>';
                    html+='</tr>';
                    html+='</table>';
                    $("#historyDiv").append(html);
                } else {
                    alert(data.respMsg);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown);
            }
        });
    }

    //获取请求参数
    function getRequestParam(searchDivId){
        var requestParam = new Object();
        $("#" + searchDivId).find("input,select").each(function () {
            if($.trim(this.value) != "" && this.name != ""){
                requestParam[this.name] = $.trim(this.value);
            }
        });
        return requestParam;
    }

    function objectIsNull(obj) {
        return (obj == null || obj == "" || typeof(obj) == "undefined" || obj == "null");
    }
</script>
</html>