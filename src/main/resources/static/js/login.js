layui.use('form', function(){
  var form = layui.form;

  //监听提交
  form.on('submit(formDemo)', function(data){
    // layer.msg(JSON.stringify(data.field));
    debugger
    axios.post('/verifyLogin', {userName: data.field.username,
                                passWord: data.field.password})
                                .then((res)=>{
                                    if(res.data=="ok") location.href='/index'
                                    else layer.msg("Invalid username or password");
                                })
    return false;
  });
});