//
//  CompanyDetailViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 27/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "CompanyDetailViewController.h"
#import "TableCustomCell.h"
@interface CompanyDetailViewController ()
{
    NSString * description,*companyName;
    UIActivityIndicatorView *activityView;
}
@end

@implementation CompanyDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    windowSize=[UIScreen mainScreen].bounds.size;
    
    self.headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, windowSize.width, 55)];
    
    self.headerView.backgroundColor = [UIColor colorWithRed:55.0f/255.0f green:105.0f/255.0f blue:147.0f/255.0f alpha:1.0f];
    
    [self.view addSubview:self.headerView];
    self.headerView.layer.shadowRadius = 5.0;
    self.headerView.layer.shadowColor = [UIColor blackColor].CGColor;
    self.headerView.layer.shadowOpacity = 0.6;
    self.headerView.layer.shadowOffset = CGSizeMake(0.0f,5.0f);
    self.headerView.layer.shadowPath = [UIBezierPath bezierPathWithRect:self.headerView.bounds].CGPath;
    
    UIButton * cancelButton=[UIButton buttonWithType:UIButtonTypeCustom];
    cancelButton.frame=CGRectMake(15, 25, 50, 25);
    cancelButton.layer.cornerRadius=5;
    cancelButton.clipsToBounds=YES;
    [cancelButton setTitle:@"Back" forState:UIControlStateNormal];
    [cancelButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelButton addTarget:self action:@selector(cancelButton) forControlEvents:UIControlEventTouchUpInside];
    [self.headerView addSubview:cancelButton];

    self.view.backgroundColor=[UIColor whiteColor];
    
    activityView =[[UIActivityIndicatorView alloc]init];
    activityView.frame=CGRectMake(windowSize.width/2-20, 150, 40, 40);
    activityView.activityIndicatorViewStyle=UIActivityIndicatorViewStyleWhiteLarge;
    activityView.color=[UIColor blackColor];
    activityView.alpha=1.0;
    [self.view addSubview:activityView];
    [self.view bringSubviewToFront:activityView];
    [activityView startAnimating];
    
    dispatch_async(dispatch_get_global_queue(0, 0),^{
        
        [self fetchCompanyDetail];
        dispatch_async(dispatch_get_main_queue(),^{
            [activityView stopAnimating];
            [self createUI];
        });
    });
    
    // Do any additional setup after loading the view.
}

-(void)cancelButton{
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)createUI{

    
    if (companyDetailTbl) {
        companyDetailTbl=nil;
    }
    companyDetailTbl=[[UITableView alloc]initWithFrame:CGRectMake(0, 57, windowSize.width, windowSize.height-50)];
    companyDetailTbl.delegate=self;
    companyDetailTbl.dataSource=self;
    companyDetailTbl.scrollEnabled=NO;
    companyDetailTbl.separatorStyle=UITableViewCellSeparatorStyleNone;
    [self.view addSubview:companyDetailTbl];
}

//Tableview delegate methods.
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

-(NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return companyName;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    CGFloat height=[self textViewHeightForText:description andWidth:windowSize.width-50];
    if (height<=100) {
        height=100;
        
    }
    return height+height;
}

//Return required heigth for display Joke
- (CGFloat)textViewHeightForText:(NSString *)text andWidth:(CGFloat)width
{
    
    UITextView *textView = [[UITextView alloc] init];
    [textView setText:text];
    CGSize size = [textView sizeThatFits:CGSizeMake(width, FLT_MAX)];
    return size.height;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TableCustomCell * cell=[tableView dequeueReusableHeaderFooterViewWithIdentifier:@"companyDetail"];
    if (!cell) {
        cell=[[TableCustomCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"companyDetail"];
    }
    CGFloat height=[self textViewHeightForText:description andWidth:windowSize.width-50];
    if (height<=100) {
        height=100;
        
    }
    cell.descriptionView.frame=CGRectMake(10, 0, windowSize.width-50, height);
    cell.descriptionView.text=description;
    return  cell;
}
//fetch company detail.
-(void)fetchCompanyDetail{
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSURL * getUrl=[NSURL URLWithString:[NSString stringWithFormat: @"https://api.linkedin.com/v1/companies/%@:(id,name,ticker,description)?format=json&oauth2_access_token=%@",self.companyId,accessToken]];
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:getUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"GET"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
   
    
    description=[response objectForKey:@"description"];
    companyName=[response objectForKey:@"name"];
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
