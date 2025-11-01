//
//  CustomVoiceController.swift
//  FindNear
//
//  Created by Phạm Hà on 31/10/25.
//

import Foundation
import UIKit
import MapboxCoreNavigation
import MapboxNavigation
import MapboxDirections
import MapboxSpeech
import AVFoundation

class CustomVoiceController: MapboxSpeechSynthesizer {
  
  // You will need audio files for as many or few cases as you'd like to handle
  // This example just covers left and right. All other cases will fail the Custom Voice Controller and
  // force a backup System Speech to kick in
  //    let turnLeft = NSDataAsset(name: "turnleft")!.data
  //    let turnRight = NSDataAsset(name: "turnright")!.data
  
  let turnLeft: Data? = nil
  let turnRight: Data? = nil
  
  override func speak(_ instruction: SpokenInstruction, during legProgress: RouteLegProgress, locale: Locale? = nil) {
    print("🗣️ Voice instruction: \(instruction.text)")
    guard let soundForInstruction = audio(for: legProgress.currentStep) else {
      // When `MultiplexedSpeechSynthesizer` receives an error from one of it's Speech Synthesizers,
      // it requests the next on the list
      delegate?.speechSynthesizer(self,
                                  didSpeak: instruction,
                                  with: SpeechError.noData(instruction: instruction,
                                                           options: SpeechOptions(text: instruction.text)))
      return
    }
    speak(instruction, data: soundForInstruction)
  }
  
  func audio(for step: RouteStep) -> Data? {
    switch step.maneuverDirection {
    case .left:
      return turnLeft
    case .right:
      return turnRight
    default:
      return nil // this will force report that Custom View Controller is unable to handle this case
    }
  }
}
