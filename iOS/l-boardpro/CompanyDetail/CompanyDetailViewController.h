//
//  CompanyDetailViewController.h
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 27/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CompanyDetailViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    CGSize windowSize;
    UITableView * companyDetailTbl;
}
@property(nonatomic,strong)NSString * companyId;
@property(nonatomic,strong)UIView *headerView;
@end
