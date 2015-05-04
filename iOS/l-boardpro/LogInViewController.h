//
//  LogInViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 27/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>



@interface LogInViewController : UIViewController<UIWebViewDelegate>
{
    UIWebView * webView;
    CGSize windowSize;
    
}
@property(nonatomic,strong)UIView * headerView;
@end
