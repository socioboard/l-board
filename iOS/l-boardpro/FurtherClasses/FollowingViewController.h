//
//  FollowingViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 27/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FollowingViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    CGSize windowSize;
    UITableView * followingTable;
}
@end
