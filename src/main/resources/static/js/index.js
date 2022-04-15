layui.use(function(){
  var layer = layui.layer
  ,form = layui.form
  ,laypage = layui.laypage
  ,element = layui.element
  ,laydate = layui.laydate
  ,util = layui.util;

  //欢迎信息
  layer.msg('Hello World');

  //delete project
  util.event('test-active', {
    'confirm': function(e){
        companyName = e.context.getAttribute('data-companyName')
        projName = e.context.getAttribute('data-projectName')
        deviceVersion = e.context.getAttribute('data-deviceVersion')
        delProject(companyName, projName, deviceVersion)
    },
  });
});


// del project
function delProject(companyName,projectName, deviceVersion){
    u = '/opt/del/'+companyName+'/'+projectName+'/'+deviceVersion
    confirmToDelete(u, function(){
        sp = document.getElementById(companyName+'_'+projectName+'_'+deviceVersion)
        sp.remove()
    })
}

// del company
function delCompany(e, companyName){
    e.stopPropagation()
    u = '/opt/delCompany/'+companyName
    confirmToDelete(u, function(){
            di = document.getElementById(companyName)
            di.remove()
    })
}


// confirm
function confirmToDelete(u, recall){
    layui.use('layer', function(){
              var layer = layui.layer;
              layer.confirm('Confirm to delete？', {
              title: 'Tip',
              icon: 2,
              btn: ['Yes', 'Cancel']
            }, function(index, layero){
            axios.get(u)
                  .then(function (response) {
                    layer.msg('delete success')
                    recall()
                  })
                  .catch(function (error) {
                    console.log(error);
                  });
            });
        });
}

// collapse all
function collapseAll(){
    var showEles = document.getElementsByClassName('layui-show')
    var allEles = document.getElementsByClassName('layui-colla-content')
    if(showEles.length==0 || showEles.length<allEles.length){
        for(var i=0;i<allEles.length;i++){
            allEles[i].classList.remove('layui-show')
            allEles[i].classList.add('layui-show')
        }
    }else {
        for(var i=0;i<allEles.length;i++){
          allEles[i].classList.remove('layui-show')
        }
    }

}
