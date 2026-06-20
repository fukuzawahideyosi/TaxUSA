<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" >
<head>
  <meta charset="UTF-8">
  <title>Panda Login Form</title>
  <link rel='stylesheet' href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&amp;display=swap'><link rel="stylesheet" href="Panda-Login-Form/style.css">

</head>
<body>
<!-- partial:index.partial.html -->

<div class="container">
    <form action="LoginServletLogic" method="post">
    <label for="username">Enter your username</label>
    <input type="text" id="username"  name="username"  placeholder="Username" autocomplete="username" required />
    <label for="password">Enter your password</label>
    <input type="password" id="password" name="password" placeholder="Password" autocomplete="current-password" required />
    <button type="submit" >Login</button>
  </form>
      <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>


<div style="display: flex1;" id="panda_jpg">
		<a href="index.html" class="navbar-brand p-0">
		<h1 class="m-0 text-uppercase text-primary"><img width="40%" src="img/panda.jpg" alt=""></h1>
		</a>
</div>

  <div class="ear-l"></div>
  <div class="ear-r"></div>
  <div class="panda-face">
    <div class="blush-l"></div>
    <div class="blush-r"></div>
    <div class="eye-l">
      <div class="eyeball-l"></div>
    </div>
    <div class="eye-r">
      <div class="eyeball-r"></div>
    </div>
    <div class="nose"></div>
    <div class="mouth"></div>
  </div>
  <div class="hand-l"></div>
  <div class="hand-r"></div>
  <div class="paw-l"></div>
  <div class="paw-r"></div>
</div>
<!-- partial -->
  <script  src="Panda-Login-Form/script.js"></script>

<script src="static/js/jquery-3.4.1.min.js"></script>
<script>

$(document).ready(function() {
	  $('#panda_jpg').css({
		    position: 'fixed',
		    top: -88,
		    left: 0,
		    width: '100%',
		    'text-align': 'center',
		    'z-index': 10000
		    ,
		  });
});

</script>

</body>
</html>
