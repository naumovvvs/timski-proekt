document.getElementById("loginForm").addEventListener("submit", function (e) {
   e.preventDefault();

   let user = document.getElementById("username").value;
   let pass = document.getElementById("password").value;


   let formData = new FormData();
   formData.append('username', user);
   formData.append('password', pass);

   $.ajax({
      url: "/login",
      type: "POST",
      data: formData,
      cache: false,
      processData: false,
      contentType: false,
      success: function (data, response) {
         // store tokens in localStorage
         console.log(response);
         window.localStorage.setItem('refreshToken', JSON.stringify(data['refreshToken']));
         window.localStorage.setItem('accessToken', JSON.stringify(data['accessToken']));
         window.location.assign("http://localhost:8080/home")
      },
      error: function (rs) {
         console.error(rs.status);
         console.error(rs.responseText);
      }
   });
});

