# -*- coding: utf-8 -*-
"""
Created on Mon Nov 24 22:41:18 2014

@author: SongQi

2014.11.30  经过一系列简单测试无误，正式开始着手编写服务器端
"""

import web
import MySQLdb
import json

urls = (
    '/','ping',
    '/query','query',
    '/add','add',
    '/update','update',
    '/ping','ping',
)

db=MySQLdb.connect(host='localhost',user='root',passwd='870923'\
,db='findcats',charset='utf8')
version=float(1)

"""
进入app时ping一下服务器
功能：
1.检查服务器是否正常工作，如果有问题，给出相应提示
2.发送app的版本号、手机IMEI号给服务器，判断当前app是否有权限或需要更新
"""
class ping:
    def GET(self):
        return 'ping accepted!'
    def POST(self):
        json_data=web.data()
        user_data=json.loads(json_data)
        print user_data
        user_version=float(user_data['version'])
        response={}
        if user_version>=version:
            response['state']=1; 
        else:
            response['state']=-1;
            
        response = json.dumps(response)
        print response       
        return response
"""
用于添加新的流浪猫喂食点/领养信息
功能：
1.添加新的流浪猫喂食点到数据库
2.添加新的领养信息到数据库
"""
class add:
    def GET(self):
        return "add accepted!"
    def POST(self):
        response={}
        json_data=web.data()
        user_data=json.loads(json_data)
        print user_data      
        if user_data['type']=='location':
            print 'add location'
            response['state']=1;                        
            cursor=db.cursor()
            args=[user_data['latitude'],user_data['longitude'],user_data['address'],
                  user_data['catnum'],user_data['babynum'],user_data['food'],
                  user_data['catcondition'],user_data['moreinfo'],user_data['updatetime']]
            attributes=' ( latitude,longitude,address,catnum,babynum,\
            food,catcondition,moreinfo,updatetime ) '
            tableName='feedplaces_beijing'
            command='insert into '+tableName+attributes+'values\
            (%s,%s,%s,%s,%s,%s,%s,%s,%s)'
            try:
                cursor.execute(command,args)
                db.commit();
            except MySQLdb.Error,e:
                response['state']=-1;
#                cursor.rollback()
                raise Exception("Execute affaris failed. Detail : %s" % e)
            finally:
                cursor.close()
        elif user_data['type']=='adoption':
            print 'add adoption'
#        暂时设置返回值一直为成功
        response = json.dumps(response) 
        return response
"""
以下功能暂时未开发
"""
class query:
    def GET(self):
        return "hello query!"
    def POST(self):
        return "hello post!"

class update:
    def GET(self):
        return "hello update!"
    def POST(self):
        return "hello post!"
        
app = web.application(urls, globals())
if __name__ == "__main__":    
    app.run()
    db.close();
else:
    application = app.wsgifunc()