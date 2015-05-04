//
//  TableCustomCell.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TableCustomCell : UITableViewCell
{
    
}
@property(nonatomic,strong)UIImageView * userImage,* feedImage,* profileImg,* menuImages;
@property(nonatomic,strong)UIButton * add_minusButton,* settingButton;
@property(nonatomic,strong)UILabel * userNameDesc,*likesLbl,* likesCount,* commentLbl,* commentCnt,* cellTitle;


@property(nonatomic,strong) UILabel * cellMenuTitle;

// job table

@property(nonatomic,strong)UILabel * cellJobLabel;

@property(nonatomic,strong)UITextView * descriptionView;
@end
