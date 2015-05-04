//
//  UpdatesViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UpdatesViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    CGSize windowSize;
    UITableView * followingTable;
    UIWebView * webView;
}
@end
