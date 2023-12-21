//需要填写你的 Access Key 和 Secret Key 这个在七牛云 个人中心>密钥管理 功能里面
qiniu.conf.ACCESS_KEY = '0kSEeVVtcqWFQ18z0TWlDe6eBC3lFchpHLBNe-_F';//公钥
qiniu.conf.SECRET_KEY = 'Y2P5am6LWW44yxo2YWwsiY4RteqyFeut58gCHBM_';//私钥

//要上传的空间
bucket = 'we-teach';//我的bucket名称,即文件的命名空间的名称
//上传到七牛后保存的文件名
key = 'my-nodejs-logo.png';//此处我直接在前台生成我需要保持的文件名,然后发到给后端。也可以把文件名直接发到后端,让后端处理,由于前端后端都是我一个人写的,所以我比较随意
//构建上传策略函数,生成token并设置回调的url以及需要回调给业务服务器的数据。此处的回调服务器地址需要你的公网服务器地址,但是我直接在本地操作的,没有公网地址,所以就没有改动七牛云的example了,直接
function uptoken(bucket, key) {
  var putPolicy = new qiniu.rs.PutPolicy(bucket+":"+key);
  putPolicy.callbackUrl = 'http://your.domain.com/callback';
  putPolicy.callbackBody = 'filename=$(fname)&filesize=$(fsize)';
  return putPolicy.token();
}
//生成上传 Token
token = uptoken(bucket, key);//此处的token需要发给前台,好让前台开始操作,实现图片上传
console.log("七牛云上传图片的token:",token);//后台代码结束


//1.选中图片时发送key，即文件名称给后台，以便后台生成token
this.axios.get('/filename','my-nodejs-logo.png')
//2.选中图片后，开始上传前：获取token
//filePath_or_stream是要上传文件的本地路径或者字节流,虽然七牛云官方文档有说最简单//的上传就是本地上传时给一个本地的路径就好了
//但是我觉得直接指定本地的图片路径只适合后台上传,不适合前台,具体原因见下面的后话
//所以本文演示的都是传全部文件数据，而非文件地址
// filePath_or_stream = './nodejs-logo.png'||filePath_or_stream=readableStream【readableStream是前台传过来的全部文件的数据流】
filePath_or_stream = readableStream;//图片的字节流数据
this.axios.get('/up/token').then(res => {
  console.log(res)
  const formdata = new FormData()//FormData是浏览器的方法，用于html追加表单键值对，详细使用可以看看MDN文档，简单介绍看看下面后话我的介绍
  formdata.append('file', req.file)//往表单上传的数据域追加file属性，它的value是req.file。 reqreq.file是我们本地的文件地址，elementui的el-upload里的http-request像我们本地计算机发起请求数据，会把我们选择的文件地址赋值给req.file（原生的html的表单上传属性也可以获取文件的地址）
  formdata.append('token', res.data)//往表单上传的数据域追加token属性，它的value是res.data，res.data是后台在我们请求token时发送过来的数据
  formdata.append('key', keyname)//往表单上传的数据域追加key属性， keyname是我们自定义需要保存的文件名称
  // 3.点击上传时：获取到凭证之后再将文件上传到七牛云空间
 //this.domain是你要上传的七牛云的空间所绑定的域名，formdata包含上传的所有数据，config用来设置请求头

  this.axios.post(this.domain, formdata, config).then(res => {
    this.imageUrl = 'http://' + this.qiniuaddr + '/' + res.data.key
    // console.log(this.imageUrl)
  })
})