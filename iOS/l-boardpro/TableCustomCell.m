//
//  TableCustomCell.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "TableCustomCell.h"

@implementation TableCustomCell

- (void)awakeFromNib {
    // Initialization code
}
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self)
    {
      
        
    if([reuseIdentifier isEqualToString:@"Feed"])
    {
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(20, 20, 60, 60);
        self.userImage.layer.cornerRadius=self.userImage.frame.size.width/2;
        self.userImage.clipsToBounds=YES;
        //self.userImage.backgroundColor=[UIColor redColor];
        [self.contentView addSubview:self.userImage];
        
        
        self.feedImage=[[UIImageView alloc]init];
        self.feedImage.frame=CGRectMake(100, 40, 90, 90);
        self.feedImage.clipsToBounds=YES;
       
        [self.contentView addSubview:self.feedImage];
        
        self.userNameDesc=[[UILabel alloc]init];
        
        self.userNameDesc.frame=CGRectMake(100, 10, 200, 40);
        [self.contentView addSubview:self.userNameDesc];
        
        
        self.likesLbl=[[UILabel alloc]init];
        //self.likesLbl.text=@"Likes";
        self.likesLbl.frame=CGRectMake(200, 40, 50, 20);
        self.likesLbl.font=[UIFont boldSystemFontOfSize:11];
        [self.contentView addSubview:self.likesLbl];
        
        self.commentLbl=[[UILabel alloc]init];
       // self.commentLbl.text=@"Commnets";
        self.commentLbl.frame=CGRectMake(200, 70, 60, 20);
        self.commentLbl.font=[UIFont boldSystemFontOfSize:11];
        [self.contentView addSubview:self.commentLbl];
        
        
        self.likesCount=[[UILabel alloc]init];
       // self.likesCount.text=@"100";
        self.likesCount.frame=CGRectMake(265, 40, 50, 20);
        self.likesCount.font=[UIFont boldSystemFontOfSize:11];
        [self.contentView addSubview:self.likesCount];
        
        self.commentCnt=[[UILabel alloc]init];
       // self.commentCnt.text=@"10";
        self.commentCnt.frame=CGRectMake(265, 70, 60, 20);
        self.commentCnt.font=[UIFont boldSystemFontOfSize:11];
        [self.contentView addSubview:self.commentCnt];
        
        
        self.add_minusButton=[[UIButton alloc]init];
        self.add_minusButton.frame=CGRectMake(self.contentView.frame.size.width-50,20,20, 20);
        
        [self.add_minusButton setBackgroundImage:[UIImage imageNamed:@"insta.png"] forState:UIControlStateNormal];
        [self.contentView addSubview:self.add_minusButton];
        
        
    }
        if ([reuseIdentifier isEqualToString:@"menuTable"]) {
            
            self.profileImg=[[UIImageView alloc]init];
            self.profileImg.frame=CGRectMake(5, 5, 30, 30);
            self.profileImg.layer.cornerRadius=self.profileImg.frame.size.width/2;
            self.profileImg.clipsToBounds=YES;
            [self.contentView addSubview:self.profileImg];
            
            self.menuImages=[[UIImageView alloc]init];
            self.menuImages.frame=CGRectMake(5, 10, 20, 20);
            self.menuImages.clipsToBounds=YES;
            [self.contentView addSubview:self.menuImages];

            
            self.cellTitle=[[UILabel alloc]init];
            self.cellTitle.textColor=[UIColor blackColor];
            self.cellTitle.frame=CGRectMake(40, 0, 100, 40);
            [self.contentView addSubview:self.cellTitle];
            
            self.cellMenuTitle=[[UILabel alloc]init];
            self.cellMenuTitle.textColor=[UIColor blackColor];
            self.cellMenuTitle.frame=CGRectMake(40, 0, 100, 40);
            self.cellMenuTitle.numberOfLines=0;
            self.cellMenuTitle.lineBreakMode=NSLineBreakByWordWrapping;
            [self.contentView addSubview:self.cellMenuTitle];
            
            self.settingButton=[[UIButton alloc]init];
            self.settingButton.frame=CGRectMake(140,5,20, 20);
            [self.settingButton setBackgroundImage:[UIImage imageNamed:@"setting.png"] forState:UIControlStateNormal];
            [self.contentView addSubview:self.settingButton];

            
        }
        if ([reuseIdentifier isEqualToString:@"job"]) {
            
            self.cellJobLabel=[[UILabel alloc]init];
            self.cellJobLabel.textColor=[UIColor blackColor];
            self.cellJobLabel.frame=CGRectMake(40, 0, self.frame.size.width-80, 40);
            self.cellJobLabel.numberOfLines=0;
            self.cellJobLabel.lineBreakMode=NSLineBreakByWordWrapping;
            self.cellJobLabel.font=[UIFont boldSystemFontOfSize:12];
            [self.contentView addSubview:self.cellJobLabel];
        }
        if ([reuseIdentifier isEqualToString:@"companyDetail"]) {
            self.descriptionView = [[UITextView alloc] init];
            self.descriptionView.scrollsToTop = NO;
            self.descriptionView.userInteractionEnabled = NO;
            self.descriptionView.textAlignment = NSTextAlignmentLeft;
            self.descriptionView.backgroundColor = [UIColor clearColor];
            [self.contentView addSubview:self.descriptionView];

        }
       
    }
    return self;
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
