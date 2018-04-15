using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using uPLibrary.Networking.M2Mqtt;
using uPLibrary.Networking.M2Mqtt.Messages;
using Windows.ApplicationModel;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Media.SpeechRecognition;
using Windows.Storage;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at https://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace Client_dieu_khien_bang_giong_noi_English_
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        static MqttClient client;
        public ushort msgId;
        public MainPage()
        {
            this.InitializeComponent();
            MqttConnect();
            InitializeSpeechRecognizer();
            txbHuongdan.Text = "\nHướng dẫn:" +
                "\nmove forward - Tiến lên\nmove backward - Lùi xuống\nturn left - Rẽ trái\nturn right - Rẽ phải " +
                "\nrotate right - Quay tròn phải\nrotate left - Quay tròn trái\nstop - Dừng lại ";


        }
        #region Mqtt

        public void MqttConnect()
        {
            client = new MqttClient("m14.cloudmqtt.com", 14953, false, MqttSslProtocols.None);
            client.ProtocolVersion = MqttProtocolVersion.Version_3_1;
            byte code = client.Connect(Guid.NewGuid().ToString(), Cons.username, Cons.password);
            client.Subscribe(new string[] { "Robot2" }, new byte[] { 0 });
            MqttPublish("Client đã kết nối Mqtt !");
            client.MqttMsgPublishReceived += client_MqttMsgPublishReceivedAsync;
        }

        private async void client_MqttMsgPublishReceivedAsync(object sender, MqttMsgPublishEventArgs e)
        {
            string ReceivedMessage = Encoding.UTF8.GetString(e.Message);
            Debug.Write(ReceivedMessage);

            switch (ReceivedMessage)
            {
                case "Client đã kết nối Mqtt !":
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
        
        async void NhanDienAsync(string result)
        {
            await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                txbNhandien.Text = result;
            });
        }

        #endregion

        #region Nhận diện giọng nói 
        private SpeechRecognizer MyRecognizer;
        private async void InitializeSpeechRecognizer()
        {
            // Initialize SpeechRecognizer Object (Khởi tạo đối tượng SpeechRecognizer)
            MyRecognizer = new SpeechRecognizer();

            // Register Event Handlers
            MyRecognizer.StateChanged += MyRecognizer_StateChanged;
            MyRecognizer.ContinuousRecognitionSession.ResultGenerated += MyRecognizer_ResultGenerated;

            // Create Grammar File Object (Tạo đối tượng Grammar từ mygrammar.xml đã xác định từ trước)
            StorageFile GrammarContentFile = await Package.Current.InstalledLocation.GetFileAsync(@"mygrammar.xml");

            // Add Grammar Constraint from Grammar File
            SpeechRecognitionGrammarFileConstraint GrammarConstraint = new SpeechRecognitionGrammarFileConstraint(GrammarContentFile);
            MyRecognizer.Constraints.Add(GrammarConstraint);

            // Compile Grammar
            SpeechRecognitionCompilationResult CompilationResult = await MyRecognizer.CompileConstraintsAsync();

            // Write Debug Information
            Debug.WriteLine("Status: " + CompilationResult.Status.ToString());

            // If Compilation Successful, Start Continuous Recognition Session
            if (CompilationResult.Status == SpeechRecognitionResultStatus.Success)
            {
                await MyRecognizer.ContinuousRecognitionSession.StartAsync();
            }

        }

        private void MyRecognizer_ResultGenerated(SpeechContinuousRecognitionSession sender, SpeechContinuousRecognitionResultGeneratedEventArgs args)
        {
            // Write Debug Information
            Debug.WriteLine(args.Result.Text);
            NhanDienAsync("Nhận dạng : "+args.Result.Text);
            // Drive robot on recognized speech
           
            switch (args.Result.Text)
            {
               case "move forward":
                    MqttPublish(args.Result.Text);break;
               case "move backward":
                    MqttPublish(args.Result.Text); break;
               case "turn right":
                    MqttPublish(args.Result.Text); break;
               case "turn left":
                    MqttPublish(args.Result.Text); break;
               case "rotate right":
                    MqttPublish(args.Result.Text); break;
               case "rote left":
                    MqttPublish(args.Result.Text); break;
               case "stop":
                    MqttPublish(args.Result.Text); break;
            }
        }
        
        private void MyRecognizer_StateChanged(SpeechRecognizer sender, SpeechRecognizerStateChangedEventArgs args)
        {
            // Write Debug Information
            Debug.WriteLine(args.State);


        }

        #endregion
    }
}
