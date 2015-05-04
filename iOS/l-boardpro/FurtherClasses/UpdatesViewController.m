//
//  UpdatesViewController.m
//  LinkedinBoard
//
//  Created by Sumit Ghosh on 29/04/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "UpdatesViewController.h"
#import "TableCustomCell.h"
#import "WebViewViewController.h"

@interface UpdatesViewController ()
{
    NSMutableArray *  companies,*companyId;
    UIActivityIndicatorView *activityView;
    WebViewViewController * webViewVC;
}
@end

@implementation UpdatesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    companies =[[NSMutableArray alloc]init];
    companyId =[[NSMutableArray alloc]init];
    
    self.view.backgroundColor=[UIColor whiteColor];
    windowSize=[UIScreen mainScreen].bounds.size;
    
    activityView =[[UIActivityIndicatorView alloc]init];
    activityView.frame=CGRectMake(windowSize.width/2-20, 150, 40, 40);
    activityView.activityIndicatorViewStyle=UIActivityIndicatorViewStyleWhiteLarge;
    activityView.color=[UIColor blackColor];
    activityView.alpha=1.0;
    [self.view addSubview:activityView];
    [self.view bringSubviewToFront:activityView];
    [activityView startAnimating];

    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(createUI) name:@"loadCompany" object:nil];
    
    // Do any additional setup after loading the view.
}

-(void)createUI{
    dispatch_async(dispatch_get_global_queue(0, 0),^{
        [self fetchFollowingData];
        dispatch_async(dispatch_get_main_queue(),^{
            [activityView stopAnimating];
            [self createTableView];
        });
    });
}

// create table to show user following company list.
-(void)createTableView{
    if (followingTable) {
        followingTable=nil;
    }
    followingTable=[[UITableView alloc]initWithFrame:CGRectMake(0, 0, windowSize.width, windowSize.height-70)];
    followingTable.delegate=self;
    followingTable.dataSource=self;
    [self.view addSubview:followingTable];
}

#pragma mark- Tabel Delegate methods

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return companies.count;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

-(NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return @"Following Companies";
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TableCustomCell * cell=[tableView dequeueReusableCellWithIdentifier:@"job"];
    if (!cell) {
        cell=[[TableCustomCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"job"];
    }
    cell.cellJobLabel.text=[companies objectAtIndex:indexPath.row];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if (webViewVC) {
        webViewVC=nil;
    }
    webViewVC=[[WebViewViewController alloc]init];
    webViewVC.companyId=[companyId objectAtIndex:indexPath.row];
    
    [self presentViewController:webViewVC animated:YES completion:nil];
    
}

#pragma mark-
-(void)fetchFollowingData{
    
    NSError * error;
    NSURLResponse * urlResponse;
    
    NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    
    NSURL * getUrl=[NSURL URLWithString:[NSString stringWithFormat: @"https://api.linkedin.com/v1/people/~:(following)?format=json&oauth2_access_token=%@",accessToken]];
    NSMutableURLRequest * request=[[NSMutableURLRequest alloc]initWithURL:getUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:50];
    [request setHTTPMethod:@"GET"];
    [request addValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSData * data=[NSURLConnection sendSynchronousRequest:request returningResponse:&urlResponse error:&error];
    
    if (data==nil) {
        return;
    }
    id response=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&error];
    NSLog(@"%@",response);
    
    NSMutableDictionary * followingDict=[response objectForKey:@"following"];
    NSMutableDictionary * companiesDict=[followingDict objectForKey:@"companies"];
    NSArray * values=[companiesDict objectForKey:@"values"];
    NSMutableDictionary * dict=[NSMutableDictionary dictionary];
    for (int i=0; i<values.count; i++) {
        dict=[values objectAtIndex:i];
        [companyId addObject:[dict objectForKey:@"id"]];
        [companies addObject:[dict objectForKey:@"name"]];
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
