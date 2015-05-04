//
//  WebViewViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WebViewViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    CGSize windowSize;
    UITableView * followingTable;
    UIWebView * webView;
    NSMutableArray * urlArr,* position;
    BOOL isWeb,isDataAvial;
}
@property(nonatomic,strong)NSURL * joburl;
@property(nonatomic,strong)UIView * headerView;
@property(nonatomic,strong)NSString * companyId;
@end
