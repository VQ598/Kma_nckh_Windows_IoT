using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using uPLibrary.Networking.M2Mqtt.Messages;
using uPLibrary.Networking.M2Mqtt;
using System.Text;
using System.Diagnostics;
using Windows.UI.Core;

// The Blank Page item template is documented at https://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace Client_dieu_khien_bang_Button
{
   
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        static MqttClient client;
        public  ushort msgId;
        public MainPage()
        {
            this.InitializeComponent();
            MqttConnect();
            txbHuongdan.Text = "\nHướng dẫn:" +
                "\nMũi tên lên - Tiến lên\nMũi tên xuống - Lùi xuống\nMũi tên trái - Rẽ trái\nMũi tên phải - Rẽ phải " +
                "\nPhím R - Quay tròn phải\nPhím L - Quay tròn trái\nEsc - Dừng lại ";


        }
        #region Mqtt

        public void MqttConnect()
        {
            client = new MqttClient(Cons.host, 14953, false, MqttSslProtocols.None);
            client.ProtocolVersion = MqttProtocolVersion.Version_3_1;
            byte code = client.Connect(Guid.NewGuid().ToString(), Cons.username, Cons.password);
            client.Subscribe(new string[] { "Robot2" }, new byte[] { 0 });
            MqttPublish("Raspberry đã kết nối Mqtt !");
            client.MqttMsgPublishReceived += client_MqttMsgPublishReceivedAsync;
        }

        private async void client_MqttMsgPublishReceivedAsync(object sender, MqttMsgPublishEventArgs e)
        {
            string ReceivedMessage = Encoding.UTF8.GetString(e.Message);
            Debug.Write(ReceivedMessage);
            
            switch (ReceivedMessage)
            {
                case "Raspberry đã kết nối Mqtt !":
                    await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
                    {
                        txbMqttStatus.Text = "Mqtt: Đã kết nối !";
                    });
                    break;
               

                default:
                    break;
            }
        }
         
        void MqttPublish(string message)
        {
            msgId = client.Publish("Robot2", // topic
                                       Encoding.UTF8.GetBytes(message), // message body
                                       MqttMsgBase.QOS_LEVEL_EXACTLY_ONCE, // QoS level
                                       false); // retained 

        }

       
        #endregion

     

        #region Button_on_Keyboard
        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            Window.Current.CoreWindow.KeyDown += HandleKeyDown;
        }

        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            base.OnNavigatedFrom(e);
            Window.Current.CoreWindow.KeyDown -= HandleKeyDown;
        }
        private void HandleKeyDown(Windows.UI.Core.CoreWindow sender, Windows.UI.Core.KeyEventArgs args)
        {

            if (args.VirtualKey == Windows.System.VirtualKey.Right)
                MqttPublish("turn right");
            else if (args.VirtualKey == Windows.System.VirtualKey.Left)
                MqttPublish("turn left");
            else if (args.VirtualKey == Windows.System.VirtualKey.Up)
                MqttPublish("move forward");
            else if (args.VirtualKey == Windows.System.VirtualKey.Down)
                MqttPublish("move backward");
            else if (args.VirtualKey == Windows.System.VirtualKey.Escape)
                MqttPublish("stop");
            else if (args.VirtualKey == Windows.System.VirtualKey.R)
                MqttPublish("rotate right");
            else if (args.VirtualKey == Windows.System.VirtualKey.L)
                MqttPublish("rotate left");
            
            
    }
        #endregion


        #region Button_Click
        private void btnMoveForward_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("move forward");
        }

        private void btnTurnleft_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("turn left");
        }

        private void btnTurnRight_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("turn right");
        }

        private void btnMoveBackward_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("move backward");
        }

        private void btnRotateLeft_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("rotate left");
        }

        private void btnStop_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("stop");
        }

        private void btnRotateRight_Click(object sender, RoutedEventArgs e)
        {
            MqttPublish("rotate right");
        }
        #endregion
    }

}
