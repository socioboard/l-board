//
//  LIAViewController.m
//  IOSLinkedInAPI-Example
//
//  Created by Jacob von Eyben on 04/27/13.
//  Copyright (c) 2013 Ancientprogramming. All rights reserved.
//

#import "ViewController.h"
#import "CustomMenuViewController.h"
#import "FeedViewController.h"
#import "SingletonClass.h"
#import "FollowingViewController.h"
#import <sqlite3.h>
#import "LogInViewController.h"
#import "UpdatesViewController.h"
#import "ShareViewController.h"

@interface ViewController (){
    sqlite3 * database;
}


@end

@implementation ViewController

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:YES];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(loginCheckComplete) name:@"loginCheckComplete" object:nil];
    [SingletonClass shareSinglton].fromAddAccount=NO;
    NSString * acceessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"access_token"];
    if (acceessToken) {
        [self retreiveDataFromSqlite];
        [self loginCheckComplete];
    }
    else{
        
        UIImageView * background=[[UIImageView alloc]initWithFrame:CGRectMake(0, 0, windowSize.width, windowSize.height)];
        background.image=[UIImage imageNamed:@"main_view.png"];
        [self.view addSubview:background];
        
        
        UIButton *loginButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        loginButton.frame = CGRectMake(20,windowSize.height-60 , 300, 44);
        [loginButton setBackgroundImage:[UIImage imageNamed:@"connect_with_linkedin.png"] forState:UIControlStateNormal];
        [loginButton addTarget:self action:@selector(didPressLogin) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:loginButton];
        
    }
}



- (void)viewDidLoad {
    [super viewDidLoad];
    windowSize=[UIScreen mainScreen].bounds.size;
    [SingletonClass shareSinglton].userName=[[NSMutableString alloc]init];
}

// didPressLogin method shows login view to user.
- (void)didPressLogin {
    
    LogInViewController * logVC=[[LogInViewController alloc]initWithNibName:@"LogInViewController" bundle:nil];
    [self presentViewController:logVC animated:YES completion:nil];
    
}

// After successful login this method loads menuView controller.
-(void)loginCheckComplete
{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"loginCheckComplete" object:nil];
    CustomMenuViewController * mainMenu=[ViewController goTOHomeView];
    [self presentViewController:mainMenu animated:YES completion:nil];
    
}

+(CustomMenuViewController*)goTOHomeView
{
    
    
    FeedViewController * feed =[[FeedViewController alloc]initWithNibName:@"FeedViewController" bundle:nil];
    feed.title=@"Profile";
    
    
    
    FollowingViewController * followingVC =[[FollowingViewController alloc]initWithNibName:@"FollowingViewController" bundle:nil];
    followingVC.title=@"Following";
    
    ShareViewController * shareVC =[[ShareViewController alloc]initWithNibName:@"ShareViewController" bundle:nil];
    shareVC.title=@"Share";
    
    UpdatesViewController * updatesVC =[[UpdatesViewController alloc]init];
    updatesVC.title=@"Job Updates";
    
    UINavigationController *followNavi = [[UINavigationController alloc] initWithRootViewController:feed];
    followNavi.navigationBar.hidden = YES;
    
    CustomMenuViewController *customMenuView =[[CustomMenuViewController alloc] init];
    customMenuView.numberOfSections = 1;
    customMenuView.viewControllers = @[feed,followingVC,shareVC,updatesVC];
    
    return customMenuView;
}

//Retreive all logged in user from sqlite DB.
-(void)retreiveDataFromSqlite{
    
    [SingletonClass shareSinglton].allData=[[NSMutableArray alloc]init];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"board11.sqlite"];
    NSString *query = [NSString stringWithFormat:@"select * from LinkBoard"];
    
    
    sqlite3_stmt *compiledStmt=nil;
    if(sqlite3_open([databasePath UTF8String], &database)!=SQLITE_OK)
        NSLog(@"error to open");
    {
        if (sqlite3_prepare_v2(database, [query UTF8String], -1, &compiledStmt, NULL)== SQLITE_OK)
        {
            NSLog(@"prepared");
            
            [[SingletonClass shareSinglton].allData removeAllObjects];
            while(sqlite3_step(compiledStmt)==SQLITE_ROW)
            {
                char *userid = (char *) sqlite3_column_text(compiledStmt,1);
                char *userfullname = (char *) sqlite3_column_text(compiledStmt,2);
                char *profilepic = (char *) sqlite3_column_text(compiledStmt,3);
                char * accesstoken=(char *)sqlite3_column_text(compiledStmt,4);
                
                NSString *userId= [NSString  stringWithUTF8String:userid];
                
                NSString *userFullName  = [NSString stringWithUTF8String:userfullname];
                NSString *profilePic  = [NSString stringWithUTF8String:profilepic];
                NSString * accessToken=[NSString stringWithUTF8String:accesstoken];
                
                NSMutableDictionary * temp=[[NSMutableDictionary alloc]init];
                [temp setObject:userId forKey:@"userId"];
                [temp setObject:userFullName forKey:@"userFullName"];
                [temp setObject:profilePic forKey:@"profilePic"];
                [temp setObject:accessToken forKey:@"accessToken"];
                
                [[SingletonClass shareSinglton].allData addObject:temp];
            }
            
        }
        sqlite3_finalize(compiledStmt);
    }
    sqlite3_close(database);
    NSLog(@"count from data base  %lu",[SingletonClass shareSinglton].allData.count);
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
