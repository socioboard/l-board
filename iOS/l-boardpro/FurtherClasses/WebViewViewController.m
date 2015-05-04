//
//  WebViewViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "WebViewViewController.h"
#import "TableCustomCell.h"

@interface WebViewViewController ()
{
    NSMutableArray *  companies,*companyId;
    UIActivityIndicatorView *activityView;
}
@end

@implementation WebViewViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    windowSize=[UIScreen mainScreen].bounds.size;
    
    
    companies =[[NSMutableArray alloc]init];
    companyId =[[NSMutableArray alloc]init];
    
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
    [cancelButton addTarget:self action:@selector(cancelButton:) forControlEvents:UIControlEventTouchUpInside];
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

    [self createUI];
    //[self createWebView];
    // Do any additional setup after loading the view.
}

-(void)createUI{
    dispatch_async(dispatch_get_global_queue(0, 0),^{
        [self getJob:self.companyId];
        dispatch_async(dispatch_get_main_queue(),^{
            [activityView stopAnimating];
            [self createTableView];
        });
    });
}

-(void)createWebView {
    isWeb=YES;
    NSURLRequest *req = [[NSURLRequest alloc]initWithURL:self.joburl];
    webView=[[UIWebView alloc]initWithFrame:CGRectMake(0, 55,windowSize.width,windowSize.height)];
    [ webView loadRequest:req];
    [self.view addSubview: webView];
}

-(void)createTableView{
    
    if (isDataAvial==NO || position.count<1) {
    
        UILabel * label=[[UILabel alloc]initWithFrame:CGRectMake(windowSize.width/2-120, 150, windowSize.width-60, 50)];
        label.text=@"Currently there are no jobs in this comapny.";
        label.font=[UIFont boldSystemFontOfSize:12];
        label.numberOfLines=0;
        label.textColor=[UIColor blackColor];
        label.lineBreakMode=NSLineBreakByWordWrapping;
        [self.view addSubview:label];
    }
    else {
            if (followingTable) {
                followingTable=nil;
            }
            followingTable=[[UITableView alloc]initWithFrame:CGRectMake(0, 55, windowSize.width, windowSize.height-80)];
            followingTable.delegate=self;
            followingTable.dataSource=self;
            [self.view addSubview:followingTable];
    }
}


#pragma mark- Tabel Delegate methods

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return urlArr.count;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return @"Job position";
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TableCustomCell * cell=[tableView dequeueReusableCellWithIdentifier:@"job"];
    if (!cell) {
        cell=[[TableCustomCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"job"];
    }
    cell.cellJobLabel.text=[position objectAtIndex:indexPath.row];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    self.joburl=[NSURL URLWithString:[urlArr objectAtIndex:indexPath.row] ];
    [self createWebView];
    
}

// get job openings on basis of company
-(void)getJob :(NSString *)compId{
    
    urlArr=[[NSMutableArray alloc]init];
    position=[[NSMutableArray alloc]init];
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSURL * getUrl=[NSURL URLWithString:[NSString stringWithFormat: @"https://api.linkedin.com/v1/companies/%@/updates?format=json&oauth2_access_token=%@",compId,accessToken]];
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:getUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"GET"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
       
        return;
    }
    isDataAvial=YES;
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
    
    NSArray * values=[response objectForKey:@"values"];
    for (int i=0; i<values.count; i++) {
        
    NSMutableDictionary * dict=[values objectAtIndex:i];
    NSMutableDictionary * updateContentDict=[dict objectForKey:@"updateContent"];
        if ([updateContentDict objectForKey:@"companyStatusUpdate"]) {
            NSLog(@" contains status");
        }
        else{
            
        
    NSMutableDictionary * companyJobUpdateDict=[updateContentDict objectForKey:@"companyJobUpdate"];
        if (companyJobUpdateDict==nil) {
             isDataAvial=NO;
            return;
        }
    NSMutableDictionary * jobDict=[companyJobUpdateDict objectForKey:@"job"];
    NSMutableDictionary * siteJobRequestDict=[jobDict objectForKey:@"siteJobRequest"];
        NSMutableDictionary * positionDict=[jobDict objectForKey:@"position"];
    [urlArr addObject:[siteJobRequestDict objectForKey:@"url"]];
    [position addObject:[positionDict objectForKey:@"title"]];
    }
    }
}



-(void)cancelButton:(UIButton*)sender{
    if (isWeb) {
        [webView removeFromSuperview];
        webView=nil;
        isWeb=NO;
    }
    else{
        [self dismissViewControllerAnimated:YES completion:nil];
        
    }
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
