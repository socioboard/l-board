//
//  FeedViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 24/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "FeedViewController.h"
#import "TableCustomCell.h"
#import "SingletonClass.h"
#import <sqlite3.h>

@interface FeedViewController ()
{
    NSMutableArray * education,* skills;
    NSString * profileImgStr;
    UIActivityIndicatorView *activityView;
    
}
@end

@implementation FeedViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    windowSize=[UIScreen mainScreen].bounds.size;
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(loadProfile) name:@"loadProfile" object:nil];
   
    activityView =[[UIActivityIndicatorView alloc]init];
    activityView.frame=CGRectMake(windowSize.width/2-20, 150, 40, 40);
    activityView.activityIndicatorViewStyle=UIActivityIndicatorViewStyleWhiteLarge;
    activityView.color=[UIColor blackColor];
    activityView.alpha=1.0;
    [self.view addSubview:activityView];
    [self.view bringSubviewToFront:activityView];
    [activityView startAnimating];
    
    NSString * firstRun=[[NSUserDefaults standardUserDefaults]objectForKey:@"firstRun"];
    if (!firstRun) {
        [self loadProfile];
        [[NSUserDefaults standardUserDefaults]setObject:@"0" forKey:@"firstRun"];
        [[NSUserDefaults standardUserDefaults]synchronize];
    }
    else{
        [self loadProfile];
    }
    
   
    // Do any additional setup after loading the view from its nib.
}

-(void)loadProfile{
    dispatch_async(dispatch_get_global_queue(0, 0),^{
        [self fetchLinkedInData];
        dispatch_async(dispatch_get_main_queue(),^{
            [activityView stopAnimating];
            [self createUI];
        });
    });
}

-(void)createUI{
    if (feedTable) {
         feedTable=nil;
    }
   
    feedTable=[[UITableView alloc]initWithFrame:CGRectMake(0, 0, windowSize.width,windowSize.height-80) style:UITableViewStylePlain];
    feedTable.dataSource=self;
    feedTable.delegate=self;
    feedTable.backgroundColor=[UIColor whiteColor];
    [self.view addSubview:feedTable];
    
    UIView * backView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, windowSize.width, 100)];
    backView.backgroundColor=[UIColor clearColor];
    
    UIImageView * profileImg=[[UIImageView alloc]initWithFrame:CGRectMake(10, 20, 40, 40)];
    profileImg.layer.cornerRadius=profileImg.frame.size.width/2;
    profileImg.clipsToBounds=YES;
    [backView addSubview:profileImg];
    
    NSURL * url=[NSURL URLWithString:[SingletonClass shareSinglton].profileImgStr];
    NSData * imageData=[NSData dataWithContentsOfURL:url];
    
    profileImg.image=[UIImage imageWithData:imageData];
    
    
    UILabel * name=[[UILabel alloc]initWithFrame:CGRectMake(50, 10, windowSize.width, 40)];
    name.text=[SingletonClass shareSinglton].userName;
    name.font=[UIFont boldSystemFontOfSize:14];
    name.textColor=[UIColor blackColor];
    [backView addSubview:name];
    
    
    UILabel * headline=[[UILabel alloc]initWithFrame:CGRectMake(50, 50, windowSize.width, 30)];
    headline.text=[SingletonClass shareSinglton].headLine;
    headline.font=[UIFont systemFontOfSize:12];
    headline.textColor=[UIColor grayColor];
    [backView addSubview:headline];
    
    feedTable.tableHeaderView=backView;
    

    
}

#pragma  mark- table delegate methods

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return 50.0;
}



-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    if (section==0)
    {
        if ([SingletonClass shareSinglton].education.count>0) {
            return  [SingletonClass shareSinglton].education.count;
        }
        else{
            return 0;
        }
    }
    else if (section==1)
    {
        if ([SingletonClass shareSinglton].skills.count>0)
        {
            return [SingletonClass shareSinglton].skills.count;
        }
        else{
            return 0;
        }

    }
    else
    {
        return 0;
        
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 45;
    
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 2;
}

-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    if (section==0) {
        return @"Education";
    }
   else if (section==1) {
        return @"Skills";
    }
   else{
       return @"";
   }
}

-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath{
    cell.textLabel.numberOfLines=0;
    cell.textLabel.lineBreakMode=NSLineBreakByWordWrapping;
    cell.textLabel.font=[UIFont systemFontOfSize:12];
    
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    UITableViewCell * cell=[tableView dequeueReusableHeaderFooterViewWithIdentifier:@"Feed"];
    
    if (cell==nil) {
        cell=[[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"Feed"];
    }
    if (indexPath.section==0) {
        cell.textLabel.text=[[SingletonClass shareSinglton].education objectAtIndex:indexPath.row];
    }
    if (indexPath.section==1) {
        cell.textLabel.text=[[SingletonClass shareSinglton].skills objectAtIndex:indexPath.row];
    }
    
    return  cell;
    
}

#pragma mark-
// fetch user profile data.
-(void)fetchLinkedInData{
    
    [SingletonClass shareSinglton].education=[[NSMutableArray alloc]init];
    [SingletonClass shareSinglton].skills=[[NSMutableArray alloc]init];
    [[SingletonClass shareSinglton].education removeAllObjects];
    [[SingletonClass shareSinglton].skills removeAllObjects];
    
    NSString * accessToken= [[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSURL * getUrl=[NSURL URLWithString:[NSString stringWithFormat:@"https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,skills,educations,languages,twitter-accounts,picture-url)?oauth2_access_token=%@&format=json",accessToken]];
    
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:getUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"GET"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
    NSLog(@" response data %@",response);
    
    NSMutableDictionary * eduDict=[NSMutableDictionary dictionary];
    NSMutableDictionary * skillDict=[NSMutableDictionary dictionary];
    NSMutableDictionary * dict=[NSMutableDictionary dictionary];
    NSMutableDictionary * skillsDict=[NSMutableDictionary dictionary];
    
    NSMutableDictionary * Dict=[NSMutableDictionary dictionary];
    
    eduDict=[response objectForKey:@"educations"];
    NSArray * valuesArr=[eduDict objectForKey:@"values"];
    
    for (int i=0; i<valuesArr.count; i++) {
        dict=[valuesArr objectAtIndex:i];
        [[SingletonClass shareSinglton].education addObject:[dict objectForKey:@"schoolName"]];
    }
    
    skillsDict=[response objectForKey:@"skills"];
    NSArray * skillValuesArr=[skillsDict objectForKey:@"values"];
    for (int j=0; j<skillValuesArr.count; j++) {
        dict=[skillValuesArr objectAtIndex:j];
        skillDict=[dict objectForKey:@"skill"];
        [[SingletonClass shareSinglton].skills addObject:[skillDict objectForKey:@"name"]];
    }
    
    [SingletonClass shareSinglton].profileImgStr =[response objectForKey:@"pictureUrl"];
    [SingletonClass shareSinglton].userName=[[NSMutableString alloc]init];
    [[SingletonClass shareSinglton].userName appendFormat:[NSString stringWithFormat:@"%@",[response objectForKey:@"firstName"]]];
    
    [[SingletonClass shareSinglton].userName appendFormat:[NSString stringWithFormat:@" %@",[response objectForKey:@"lastName"]]];
    [SingletonClass shareSinglton].headLine=[response objectForKey:@"headline"];
    [SingletonClass shareSinglton].userId=[response objectForKey:@"id"];
    
    
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
