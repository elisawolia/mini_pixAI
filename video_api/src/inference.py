import torch
from diffusers import StableDiffusionPipeline, DPMSolverMultistepScheduler

import config

pipe = StableDiffusionPipeline.from_pretrained(config.model_id,
                                               safety_checker=None
                                               ).to(config.device)

pipe.scheduler = DPMSolverMultistepScheduler.from_config(
    pipe.scheduler.config, use_karras_sigmas=True
)
pipe.enable_attention_slicing()


def generate_images(parameters):
    script = [
        f"modern disney happy running {parameters['color']} {parameters['animal']} on yellow road full-size image",
        f"modern disney happy running {parameters['color']} {parameters['animal']} on wet grass full-size image",
        f"modern disney happy running {parameters['color']} {parameters['animal']} near the castle full-size image",
        f"modern disney happy sitting {parameters['color']} {parameters['animal']} looking in the camera full-size image",
        f"modern disney unhappy sitting {parameters['color']} {parameters['animal']} looking in the camera full-size image"
    ]

    generator = [torch.Generator(config.device).manual_seed(0) for _ in range(len(script))]

    images = pipe(script, num_inference_steps=config.steps, generator=generator).images
    final = [
        {
           'image': image,
           'type': idx < len(script)
        } for idx, image in enumerate(images)]

    return final


if __name__ == '__main__':
    generate_images({
        'animal': 'bird',
        'color': 'white'
    })

