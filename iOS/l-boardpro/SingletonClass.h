//
//  SingletonClass.h
//  TwitterBoard
//
//  Created by Sumit Ghosh on 18/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SingletonClass : NSObject

+(SingletonClass*)shareSinglton;

@property(nonatomic,strong)NSString * userId,* headLine;
@property(nonatomic,strong)NSMutableString * userName;

@property(nonatomic,strong)NSMutableArray * allData;
@property(nonatomic)BOOL fromAddAccount;

@property(nonatomic,strong)NSMutableArray * education,* skills;
@property(nonatomic,strong)NSString * profileImgStr;

@property(nonatomic,strong)NSString * userProImg;
@end
