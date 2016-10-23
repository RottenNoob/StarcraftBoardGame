<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div>
	<input type="text" id="messageinput" autocomplete="off"/>
</div>
<div>
	<button type="button" onclick="send();" >Send a message</button>
</div>
<textarea rows="10" cols="75" id="messages" style="resize: none;" readonly></textarea>