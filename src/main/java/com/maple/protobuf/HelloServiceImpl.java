package com.maple.protobuf;

import com.google.protobuf.*;

/**
 * @author maple 2018.09.04 下午9:50
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase{

    @Override
    public void foo(HelloServiceOuterClass.FooRequest request, io.grpc.stub.StreamObserver<HelloServiceOuterClass.FooResponse> responseObserver) {
        super.foo(request, responseObserver);
    }
}
