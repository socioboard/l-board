//
//  AppDelegate.h
//  l-boardpro
//
//  Created by Sumit Ghosh on 04/05/15.
//  Copyright (c) 2015 Sumit Ghosh. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import <sqlite3.h>
#import "MBProgressHUD.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>
{
    MBProgressHUD * HUD;
}
@property (strong, nonatomic) UIWindow *window;
-(void) showHUDLoadingView:(NSString *)strTitle;
-(void) hideHUDLoadingView;
-(void)showToastMessage:(NSString *)message;
+(AppDelegate *)sharedAppDelegate;
@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;


@end

