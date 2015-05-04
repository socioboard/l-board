//
//  FeedViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 24/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <sqlite3.h>

@interface FeedViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    CGSize windowSize;
    UITableView * feedTable;
    sqlite3 * database;
   
}
@end
