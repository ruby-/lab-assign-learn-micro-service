namespace java edu.uta.cse.thrift.message
namespace py msg.service.api

service MSGService {

    bool sendMobileMSG(1:string mobile, 2:string msg)

    bool sendEmailMessage(1:string email, 2:string msg)
}